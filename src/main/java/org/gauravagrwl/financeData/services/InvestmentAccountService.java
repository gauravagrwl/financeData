package org.gauravagrwl.financeData.services;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.helper.DuplicateStatementRecords;
import org.gauravagrwl.financeData.helper.FinanceAppHelper;
import org.gauravagrwl.financeData.helper.FinanceAppQuery;
import org.gauravagrwl.financeData.helper.InvestmentBalanceCalculatedRecords;
import org.gauravagrwl.financeData.helper.enums.TransactionType;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.model.userAccounts.reports.HoldingReportStatement;
import org.gauravagrwl.financeData.model.userAccounts.statements.BankAccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.statements.InvestmentAccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
@Slf4j
public class InvestmentAccountService {

    @Autowired
    MongoTemplate template;

    /**
     * Need to calculate account statment
     *
     * @param userAccount
     */
    public void updateInvestmentAccountDetails(UserAccount userAccount) {
        List<InvestmentBalanceCalculatedRecords> records = investmentBalanceAggregationQuery(userAccount).getMappedResults();
        BigDecimal cashInvested = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
        BigDecimal amountInvested = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
        BigDecimal cashReturn = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
        BigDecimal amountReturn = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
        BigDecimal optionReturn = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
        BigDecimal otherCharges = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
        for (InvestmentBalanceCalculatedRecords record : records) {
            log.info("updateInvestmentAccountDetails: {}", record.toString());
            TransactionType type = TransactionType.valueOf(record.getId());
            switch (type) {
                case DEPOSIT -> {
                    cashInvested = cashInvested.add(record.getTotal());
                }
                case WITHDRAWAL -> {
                    cashReturn = cashReturn.add(record.getTotal());
                }
                case FAILED -> {
                    cashInvested = cashInvested.subtract(record.getTotal());
                }
                case BUY -> {
                    amountInvested = amountInvested.add(record.getTotal());
                }
                case OBTC -> {
                    optionReturn = optionReturn.subtract(record.getTotal());
                }
                case SELL -> {
                    amountReturn = amountReturn.add(record.getTotal());
                }
                case OSTO -> {
                    optionReturn = optionReturn.add(record.getTotal());
                }
                case EARN -> {
                    amountReturn = amountReturn.add(record.getTotal());
                }
                case EARN_REVERT -> {
                    amountReturn = amountReturn.subtract(record.getTotal());
                }
                case CHARGES -> {
                    otherCharges = otherCharges.add(record.getTotal());
                }

                case OTHERS -> {
                    log.info("Need to decide");
                }
                case CANCEL -> {
                    log.info("Need to decide");
                }
                case STAKE_DEPOSIT -> {
                    log.info("Need to decide");
                }
                case STAKE_WITHDRAWAL -> {
                    log.info("Need to decide");
                }
                default -> throw new FinanceAppException("No transaction type defined for: {}", type.name());
            }
        }
        BigDecimal netCashProfitLoss = cashReturn.subtract(cashInvested);
        BigDecimal netAmountProfitLoss = amountReturn.subtract(amountInvested);
        UpdateDefinition updateDefinition = Update.update("cashInvested", cashInvested)
                .set("amountInvested", amountInvested)
                .set("optionStakeReturn", optionReturn)
                .set("cashReturn", cashReturn)
                .set("amountReturn", amountReturn)
                .set("netAmountProfitLoss", netAmountProfitLoss)
                .set("otherCharges", otherCharges)
                .set("netCashProfitLoss", netCashProfitLoss);
        UpdateResult updateResult = template.updateFirst(FinanceAppQuery.findByIdQuery(userAccount.getId()), updateDefinition, UserAccount.class);
        log.info("Account Balance Updated {}", updateResult.wasAcknowledged());
    }

    public void insertInvestmentAccountReportStatement(UserAccount userAccount) {
        List<InvestmentAccountStatement> accountStatementList =
                template.find(FinanceAppQuery.findAndSortAllInvestmentStatementQuery(userAccount), InvestmentAccountStatement.class);
        List<InvestmentAccountStatement> accountStatementForProcessingList = accountStatementList.stream()
                .filter(
                        accountStatement ->
                                (null == accountStatement.getReportStatement() || null == accountStatement.getReportStatement().getId())
                ).toList();

        Map<String, List<InvestmentAccountStatement>> groupByInstrument = accountStatementForProcessingList
                .stream().filter(s -> null != s.getInstrument())
                .collect(Collectors.groupingBy(InvestmentAccountStatement::getInstrument));

        List<HoldingReportStatement> holdingReportStatementList = new ArrayList<>();

        groupByInstrument.forEach((s, investmentAccountStatements) -> {
            if (!s.isBlank()) {
                HoldingReportStatement holdingReportStatement = template.findOne(FinanceAppQuery.findByInstrumentName(s), HoldingReportStatement.class);
                if (holdingReportStatement == null) {
                    holdingReportStatement = new HoldingReportStatement();
                    holdingReportStatement.setAccountId(userAccount.getId());
                    holdingReportStatement.setInstrument(s);
                }
                holdingReportStatement.getAccountStatementIdList().addAll(investmentAccountStatements.stream().map(InvestmentAccountStatement::getId).toList());
                updateHoldingRecords(holdingReportStatement);
                holdingReportStatementList.add(holdingReportStatement);
            }
        });
        //TODO: Get Holding Report Statement if holding is already added.
        for (HoldingReportStatement holdingReportStatement : holdingReportStatementList) {
            if (holdingReportStatement.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                holdingReportStatement.setAmount(BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
                holdingReportStatement.setAvgBuyPrice(BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
                holdingReportStatement.setQuantity(BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
            }
            template.insert(holdingReportStatement);

        }

        log.info("Building Report Data for {}", userAccount.getAccountDisplayName());
    }

    public void updateDuplicateInvestmentRecords(UserAccount userAccount) {
        List<DuplicateStatementRecords> duplicateRecordsList = getDuplicateRecordsList(userAccount);
        for (DuplicateStatementRecords duplicateStatementRecords : duplicateRecordsList) {
            duplicateStatementRecords.getIds().forEach(id -> {
                UpdateResult updateResult = template.updateFirst(FinanceAppQuery.findByIdQuery(id), FinanceAppQuery.updateBooleanValueIndicator("duplicate", Boolean.TRUE), InvestmentAccountStatement.class);
                if (updateResult.wasAcknowledged()) {
                    log.info("Success: Duplicate Indicator updated for id: {} ", id);
                } else {
                    log.info("Failure: Duplicate Indicator update for id: {} ", id);
                }
            });
        }
    }

    private List<DuplicateStatementRecords> getDuplicateRecordsList(UserAccount userAccount) {
        Aggregation duplicateRecordAggregation = FinanceAppQuery.findDuplicateTransactionAggregationQuery(userAccount);
        log.info(duplicateRecordAggregation.toString());
        return template.aggregate(duplicateRecordAggregation, InvestmentAccountStatement.class, DuplicateStatementRecords.class).getMappedResults();
    }

    AggregationResults<InvestmentBalanceCalculatedRecords> investmentBalanceAggregationQuery(UserAccount userAccount) {
        MatchOperation matchAccountId = Aggregation.match(Criteria.where("accountId").is(userAccount.getId()));
        GroupOperation groupOperation = group("transactionType").sum("$amount").as("total");
        ProjectionOperation projectToUser = Aggregation.project("ids", "total");
        Aggregation investmentAccountBalanceAggregation = newAggregation(matchAccountId, groupOperation, projectToUser);
        AggregationResults<InvestmentBalanceCalculatedRecords> aggregate = template.aggregate(investmentAccountBalanceAggregation, InvestmentAccountStatement.class, InvestmentBalanceCalculatedRecords.class);
        return aggregate;
    }

    public void deleteInvestmentAccountStatement(UserAccount userAccount, String accountStatementId) {
        DuplicateStatementRecords duplicateStatementRecord = getDuplicateRecordsList(userAccount).stream().filter(r -> r.getIds().contains(accountStatementId)).findFirst().get();

        InvestmentAccountStatement accountStatement = template.findOne(FinanceAppQuery.findByIdQuery(accountStatementId), InvestmentAccountStatement.class);
        String accountTransactionId = "1111";
        template.remove(FinanceAppQuery.findByIdQuery(accountTransactionId), AccountTransaction.class, userAccount.getAccountTransactionCollectionName());

        String reportId = accountStatement.getReportStatement().getId();
        HoldingReportStatement holdingReportStatement = template.findOne(FinanceAppQuery.findByIdQuery(reportId), HoldingReportStatement.class);
        holdingReportStatement.getAccountStatementIdList().remove(accountStatementId);

        template.remove(FinanceAppQuery.findByIdQuery(accountStatementId), BankAccountStatement.class);
        updateHoldingRecords(holdingReportStatement);
        updateInvestmentAccountDetails(userAccount);

        if (duplicateStatementRecord.getCount() == 2) {
            duplicateStatementRecord.getIds().remove(accountStatementId);
            String id = duplicateStatementRecord.getIds().get(0);
            UpdateResult updateResult = template.updateFirst(
                    FinanceAppQuery.findByIdQuery(id),
                    FinanceAppQuery.updateBooleanValueIndicator("duplicate", Boolean.FALSE),
                    BankAccountStatement.class);
        }
    }

    private void updateHoldingRecords(HoldingReportStatement holdingReportStatement) {
        //TODO: Calculate the average cost and other details of that holding.
        for (String statementId : holdingReportStatement.getAccountStatementIdList()) {
            InvestmentAccountStatement statement = template.findOne(FinanceAppQuery.findByIdQuery(statementId), InvestmentAccountStatement.class);
            log.info("Processing Holding details for: {}", statement.toString());
            switch (statement.getTransactionType()) {
                case BUY -> {
                    if (holdingReportStatement.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                        holdingReportStatement.setAvgBuyPrice(BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
                        holdingReportStatement.setAmount(BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
                    }
                    holdingReportStatement.setQuantity(holdingReportStatement.getQuantity().add(statement.getQuantity()));
                    holdingReportStatement.setAmount(holdingReportStatement.getAmount().add(statement.getAmount()).add(statement.getFee()));
                    holdingReportStatement.setAvgBuyPrice(holdingReportStatement.getAmount().divide(holdingReportStatement.getQuantity(), RoundingMode.HALF_UP));
                    holdingReportStatement.setProfitLoss(statement.getAmount().subtract(holdingReportStatement.getAmount()));
                }
                case SELL -> {
                    holdingReportStatement.setProfitLoss(statement.getAmount().subtract(holdingReportStatement.getAmount()));
                    holdingReportStatement.setQuantity(holdingReportStatement.getQuantity().subtract(statement.getQuantity()));
                    holdingReportStatement.setAmount(holdingReportStatement.getAmount().subtract(statement.getAmount()));

                }
                case OBTC -> {
                    // Option Buy to close open Sell - Call contract.
                    holdingReportStatement.setAmount(holdingReportStatement.getAmount().add(statement.getAmount()));
                    holdingReportStatement.setOptionEarning(holdingReportStatement.getOptionEarning().subtract(statement.getAmount()));
                }
                case OSTO -> {
                    // Option Sell - Call contract.
                    holdingReportStatement.setAmount(holdingReportStatement.getAmount().subtract(statement.getAmount()));
                    holdingReportStatement.setOptionEarning(holdingReportStatement.getOptionEarning().add(statement.getAmount()));
                }
                case EARN -> {
                    holdingReportStatement.setAmount(holdingReportStatement.getAmount().add(statement.getAmount()));
                    holdingReportStatement.setQuantity(holdingReportStatement.getQuantity().add(statement.getQuantity()));

                    holdingReportStatement.setStakeQuantity(holdingReportStatement.getStakeQuantity().add(statement.getQuantity()));
                    holdingReportStatement.setStakeEarning(holdingReportStatement.getStakeEarning().add(statement.getAmount()));

                    if (holdingReportStatement.getQuantity().compareTo(BigDecimal.ZERO) == 1)
                        holdingReportStatement.setAvgBuyPrice(holdingReportStatement.getAmount().divide(holdingReportStatement.getQuantity(), FinanceAppHelper.currencyScale, RoundingMode.UP));

                }
                case EARN_REVERT -> {
                    holdingReportStatement.setAmount(holdingReportStatement.getAmount().subtract(statement.getAmount()));
                    holdingReportStatement.setQuantity(holdingReportStatement.getQuantity().subtract(statement.getQuantity()));

                    holdingReportStatement.setStakeQuantity(holdingReportStatement.getStakeQuantity().subtract(statement.getQuantity()));
                    holdingReportStatement.setStakeEarning(holdingReportStatement.getStakeEarning().subtract(statement.getAmount()));
                    if (holdingReportStatement.getQuantity().compareTo(BigDecimal.ZERO) == 1)
                        holdingReportStatement.setAvgBuyPrice(holdingReportStatement.getAmount().divide(holdingReportStatement.getQuantity(), FinanceAppHelper.currencyScale, RoundingMode.UP));

                }
                case STAKE_DEPOSIT -> {
                    holdingReportStatement.setStakeQuantity(holdingReportStatement.getStakeQuantity().add(statement.getQuantity()));
                }
                case STAKE_WITHDRAWAL -> {
                    holdingReportStatement.setStakeQuantity(holdingReportStatement.getStakeQuantity().subtract(statement.getQuantity()));
                }

//                    case Sell -> amountReturn = amountReturn.add(record.getTotal());
//                    case Deposit -> cashInvested = cashInvested.add(record.getTotal());
//                    case Withdrawal -> cashReturn = cashReturn.add(record.getTotal());
//                    case Failed -> cashInvested = cashInvested.subtract(record.getTotal());
//                    case OBTC -> optionReturn = optionReturn.subtract(record.getTotal());
//                    case OSTO -> optionReturn = optionReturn.add(record.getTotal());
                default -> log.error("No rule define for statement: {}", statement.toString());
            }
        }

    }
}
//public void calculateHolding(StatementModel accountStatement) {
//    StockInvestmentAccountStatementModel s = (StockInvestmentAccountStatementModel) accountStatement;
//    log.info(s.toString());
//    switch (s.getC_trans_code()) {
//        case Buy -> {
//            totalQuantity = totalQuantity.add(s.getC_quantity());
//            totalAmount = totalAmount.add(s.getC_amount().abs());
//            averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
//        }
//        case Sell -> {
//            if (totalQuantity.compareTo(s.getC_quantity()) == 0) {
//                // Complete Sold-out
//                profitLoss = profitLoss.add(s.getC_amount().subtract(totalAmount)).setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
//                averageRate = totalQuantity = totalAmount = BigDecimal.ZERO;
//            } else if (totalQuantity.compareTo(s.getC_quantity()) == 1) {
//                // calculate profilt on that partial sold.
//                profitLoss = profitLoss.add(s.getC_amount().subtract(s.getC_quantity().multiply(averageRate))).setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
//                // Partial sold
//                totalQuantity = totalQuantity.subtract(s.getC_quantity());
//                totalAmount = totalQuantity.multiply(averageRate);
//            } else {
//                need_correction = Boolean.TRUE;
//            }
//        }
//        case Earn -> {
//            totalAmount = totalAmount.add(s.getC_amount().abs());
//            totalQuantity = totalQuantity.add(s.getC_quantity());
//            if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
//                averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
//            }
//        }
//        case Charge -> {
//            totalAmount = totalAmount.subtract(s.getC_amount().abs());
//            if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
//                averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
//            }
//        }
//        case O_STO -> {
//            optionAmount = optionAmount.add(s.getC_amount());
//        }
//        case O_BTC -> {
//            optionAmount = optionAmount.subtract(s.getC_amount().abs());
//        }
//        case Stake -> {
//            totalQuantityStake = totalQuantityStake.add(s.getC_stake_quantity());
//        }
//        case StakeWithdrawl -> {
//            totalQuantityStake = totalQuantityStake.subtract(s.getC_stake_quantity());
//            if (totalQuantityStake.compareTo(BigDecimal.ZERO) == 0) {
//                totalQuantityStake = BigDecimal.ZERO.setScale(2);
//            }
//        }
//        default -> {
//            log.info("No rule define for statement: " + s);
//        }
//
//    }
//    log.info(this.toString());