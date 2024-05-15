package org.gauravagrwl.financeData.model.accountCollection;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountStatementModel.StockInvestmentAccountStatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.investment.CoinbaseAccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.investment.CryptoAppAccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.investment.RobinhoodStockAccountStatementTransaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class supports below Institution Sub Category:
 * <p>
 * STOCK(InstitutionCategoryEnum.INVESTMENT, "STOCK", "201"),
 * CRYPTO(InstitutionCategoryEnum.INVESTMENT, "CRYPTO", "202"),
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Slf4j
public class InvestmentAccountCollection extends AccountCollection {

    // Total Amount Invested.
    private BigDecimal amountInvestment = BigDecimal.ZERO;
    private BigDecimal cashInvestment = BigDecimal.ZERO;

    // Total Amount Returned.
    private BigDecimal amountReturn = BigDecimal.ZERO;
    private BigDecimal cashReturn = BigDecimal.ZERO;

    // Is this Account Auto Tradeable.
    private Boolean isAutoTradable = Boolean.FALSE;

    @Override
    public MappingStrategy<? extends AccountStatementTransaction> getHeaderColumnNameModelMappingStrategy() {
        switch (getProfileType()) {
            case "Robinhood_STOCK" -> {
                MappingStrategy<RobinhoodStockAccountStatementTransaction> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<RobinhoodStockAccountStatementTransaction>()
                        .withForceCorrectRecordLength(true).build();
                headerColumnNameMappingStrategy.setProfile(getProfileType());
                headerColumnNameMappingStrategy.setType(RobinhoodStockAccountStatementTransaction.class);
                return headerColumnNameMappingStrategy;
            }
            case "CryptoApp_CRYPTO" -> {
                MappingStrategy<CryptoAppAccountStatementTransaction> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<CryptoAppAccountStatementTransaction>()
                        .withForceCorrectRecordLength(true).build();
                headerColumnNameMappingStrategy.setProfile(getProfileType());
                headerColumnNameMappingStrategy.setType(CryptoAppAccountStatementTransaction.class);
                return headerColumnNameMappingStrategy;
            }
            case "Coinbase_CRYPTO" -> {
                MappingStrategy<CoinbaseAccountStatementTransaction> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<CoinbaseAccountStatementTransaction>()
                        .withForceCorrectRecordLength(true).build();
                headerColumnNameMappingStrategy.setProfile(getProfileType());
                headerColumnNameMappingStrategy.setType(CoinbaseAccountStatementTransaction.class);
                return headerColumnNameMappingStrategy;
            }

            default -> {
                return null;
            }
        }
    }

    @Override
    public Update updateAccountBalanceDefination() {
        return new Update().set("amountInvestment", this.getAmountInvestment()).set("cashInvestment", this.getCashInvestment()).set("cashReturn", this.getCashReturn());
    }

    @Override
    public Update updateAccountTranBalanceDefination(StatementModel statementModel) {
        return null;
    }

    @Override
    public Query findDuplicateRecordQuery(AccountStatementTransaction statementModel) {
        switch (getProfileType()) {
            case "Robinhood_STOCK" -> {
                RobinhoodStockAccountStatementTransaction statement = (RobinhoodStockAccountStatementTransaction) statementModel;
                return new Query(
                        Criteria.where("s_activity_Date").is(statement.getS_activity_Date())
                                .and("s_process_Date").is(statement.getS_process_Date())
                                .and("s_settle_Date").is(statement.getS_settle_Date())
                                .and("s_instrument").is(statement.getS_instrument())
                                .and("s_description").is(statement.getS_description())
                                .and("s_trans_Code").is(statement.getS_trans_Code())
                                .and("s_quantity").is(statement.getS_quantity())
                                .and("s_price").is(statement.getS_price())
                                .and("s_amount").is(statement.getS_amount().abs()));
            }
            case "CryptoApp_CRYPTO" -> {
                CryptoAppAccountStatementTransaction statement = (CryptoAppAccountStatementTransaction) statementModel;
                return new Query(
                        Criteria.where("s_timestamp").is(statement.getS_timestamp())
                                .and("s_transaction_description").is(statement.getS_transaction_description())
                                .and("s_crypto_currency").is(statement.getS_crypto_currency())
                                .and("s_amount").is(statement.getS_amount())
                                .and("s_to_Currency").is(statement.getS_to_Currency())
                                .and("s_to_Amount").is(statement.getS_to_Amount())
                                .and("s_native_Currency").is(statement.getS_native_Currency())
                                .and("s_native_Amount").is(statement.getS_native_Amount())
                                .and("s_native_Amount_USD").is(statement.getS_native_Amount_USD())
                                .and("s_transaction_kind").is(statement.getS_transaction_kind())
                                .and("s_transaction_hash").is(statement.getS_transaction_hash()));
            }
            case "Coinbase_CRYPTO" -> {
                CoinbaseAccountStatementTransaction statement = (CoinbaseAccountStatementTransaction) statementModel;
                return new Query(
                        Criteria.where("s_coinbase_ID").is(statement.getS_coinbase_ID())
                );
            }
            default -> {
                return null;
            }
        }
    }


    @Override
    public BigDecimal getAccountStatementBalance() {
        return getAmountInvestment();
    }

    @Override
    public void calculateAndUpdateAccountStatements(List<StatementModel> statementModelList) {
        updateStockStatement(statementModelList);
        setUpdateAccountStatement(Boolean.TRUE);
//        switch (getProfileType()) {
//            case "Robinhood_STOCK" -> {
//                updateStockStatement(statementModelList);
//                setUpdateAccountStatement(Boolean.TRUE);
//
//            }
//        }
    }

    @Override
    public void updateNeededFlags(Boolean updateAppAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport) {
        this.setUpdateAccountStatementModelNeeded(updateAppAccountStatement);
        this.setUpdateAccountReportNeeded(updateAccountReport);
        this.setUpdateCashFlowReportNeeded(updateCashFlowReport);
    }

    /**
     * Buy: instrument(assets / coin) added under any transaction code.
     * Sell: instrument(assets / coin) removed or sold under any transaction code.
     * O_*: Option transactions
     * Transfer: instrument(assets / coin) is transfer
     * Earn: Div, REC, SLIP, MISC
     * Charge:DTAX,
     */
    private void updateStockStatement(List<StatementModel> statementList) {
        for (StatementModel trans_s : statementList) {
            StockInvestmentAccountStatementModel s = (StockInvestmentAccountStatementModel) trans_s;
            log.info(s.toString());
            switch (s.getC_trans_code()) {
                case Buy, O_BTC, Earn -> {
                    if (s.getC_amount().compareTo(BigDecimal.ZERO) != 0)
                        amountInvestment = amountInvestment.add(s.getC_amount());
                }
                case Sell, O_STO, Charge -> {
                    if (s.getC_amount().compareTo(BigDecimal.ZERO) != 0)
                        amountInvestment = amountInvestment.subtract(s.getC_amount());
                }
                case Deposit -> {
                    cashInvestment = cashInvestment.add(s.getC_amount());
                }
                case O_EXP, O_ASGN, Stake, StakeWithdrawl -> {
                    log.warn("Do nothing!!!");
                }
                case Cancel -> {
                    cashInvestment = cashInvestment.subtract(s.getC_amount());
                }
                case Withdrawal -> {
                    cashReturn = cashReturn.add(s.getC_amount());
                }
                default -> {
                    log.warn("Transaction Code not set for transaction: " + s.toString());
                }
            }
        }
        log.info(toString());
    }

    @Override
    public Query transactionSortQuery() {
        Sort sort = null;
        Query query = new Query();
        switch (getProfileType()) {
            case "Robinhood_STOCK" -> {
                sort = Sort.by(Sort.Direction.ASC, "s_settle_Date").and(Sort.by(Sort.Direction.ASC, "s_trans_Code"));
            }
            case "CryptoApp_CRYPTO" -> {
                sort = Sort.by(Sort.Direction.ASC, "s_timestamp").and(Sort.by(Sort.Direction.ASC, "s_transaction_kind"));
            }
        }
        return (sort != null) ? query.with(sort) : query;
    }

    @Override
    public Query statementModelSort() {
        return new Query().with(Sort.by(Sort.Direction.ASC, "c_transaction_dateTime").and(Sort.by(Sort.Direction.ASC, "c_trans_code")));
    }

    @Override
    public void resetFields() {
        this.setUpdateAccountStatementModelNeeded(Boolean.FALSE);
        this.setUpdateAccountReportNeeded(Boolean.FALSE);
        this.setUpdateCashFlowReportNeeded(Boolean.FALSE);
        this.setHardStopDate(null);
        this.setIsActive(Boolean.TRUE);
        this.setBalanceCalculated(Boolean.FALSE);

        amountInvestment = BigDecimal.ZERO;
        cashInvestment = BigDecimal.ZERO;

        // Total Amount Returned.
        amountReturn = BigDecimal.ZERO;
        cashReturn = BigDecimal.ZERO;
        // Is this Account Auto Tradeable.
        isAutoTradable = Boolean.FALSE;

    }

    @Override
    public String toString() {
        return "InvestmentAccountCollection{" +
                "amountInvestment=" + amountInvestment +
                ", cashInvestment=" + cashInvestment +
                ", amountReturn=" + amountReturn +
                ", cashReturn=" + cashReturn +
                ", isAutoTradable=" + isAutoTradable +
                '}';
    }
}
