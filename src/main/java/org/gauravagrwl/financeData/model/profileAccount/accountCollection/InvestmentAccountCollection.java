package org.gauravagrwl.financeData.model.profileAccount.accountCollection;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.helper.AccountTypeEnum;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.CryptoHoldingCollection;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.HoldingTransactions;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.ReportCollection;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.StockHoldingCollection;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.InvestmentCryptoAccountStatement;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.InvestmentStockAccountStatement;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(String mappingProfile) {
        if (StringUtils.containsIgnoreCase(mappingProfile, "Stock")) {
            MappingStrategy<InvestmentStockAccountStatement> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<InvestmentStockAccountStatement>()
                    .withForceCorrectRecordLength(true).build();
            headerColumnNameMappingStrategy.setProfile(mappingProfile);
            headerColumnNameMappingStrategy.setType(InvestmentStockAccountStatement.class);
            return headerColumnNameMappingStrategy;
        } else {
            MappingStrategy<InvestmentCryptoAccountStatement> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<InvestmentCryptoAccountStatement>()
                    .withForceCorrectRecordLength(true).build();
            headerColumnNameMappingStrategy.setProfile(mappingProfile);
            headerColumnNameMappingStrategy.setType(InvestmentCryptoAccountStatement.class);
            return headerColumnNameMappingStrategy;
        }
    }

    @Override
    public Update retrieveUpdateAccountDocumentQuery() {
        return new Update().set("amountInvestment", this.getAmountInvestment()).set("cashInvestment", this.getCashInvestment()).set("cashReturn", this.getCashReturn());
    }

    @Override
    public Update getUpdateAccountStatementQuery(AccountStatementDocument accountStatementDocument) {
        if (AccountTypeEnum.STOCK.compareTo(this.getAccountType()) == 0) {
            InvestmentStockAccountStatement statement = (InvestmentStockAccountStatement) accountStatementDocument;
            return Update.update("fee", statement.getFee());
        } else {
            return null;
        }
    }

    @Override
    public Query findDuplicateRecordQuery(AccountStatementDocument statementDocument) {
        return null;
    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return getAmountInvestment();
    }

    @Override
    public List<? extends AccountStatementDocument> calculateAndUpdateAccountStatements(List<? extends AccountStatementDocument> statementDocumentList) {
        if (AccountTypeEnum.STOCK.compareTo(this.getAccountType()) == 0) {
            List<InvestmentStockAccountStatement> statementList = (List<InvestmentStockAccountStatement>) statementDocumentList;
            updateStockStatement(statementList);
            //Stock processing
            //TODO: Update statement
            //TODO: Update Total amount invested
            //TODO: Update Total amount return
            return statementList;
        } else {
            List<InvestmentCryptoAccountStatement> statementList = (List<InvestmentCryptoAccountStatement>) statementDocumentList;
            //TODO: Update statement
            //TODO: Update Total amount invested
            //TODO: Update Total amount return
            return statementList;
        }


    }

    @Override
    public List<? extends ReportCollection> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList) {
        if (AccountTypeEnum.STOCK.compareTo(this.getAccountType()) == 0) {
            List<InvestmentStockAccountStatement> statementList = (List<InvestmentStockAccountStatement>) accountStatementList;

            Map<String, List<InvestmentStockAccountStatement>> instrument_Trans =
                    statementList.stream().collect(Collectors.groupingBy(InvestmentStockAccountStatement::getStock_instrument));
            //Stock processing
            //TODO: Update holding status
            List<StockHoldingCollection> stockHoldingCollectionList = new ArrayList<>();
            instrument_Trans.remove("");
            Set<String> instruments = instrument_Trans.keySet();
            for (String s : instruments) {
                StockHoldingCollection holdingDocument = new StockHoldingCollection();
                holdingDocument.setAccountDocumentId(this.getId());
                holdingDocument.setInstrument(s);
                holdingDocument.getHoldingTransactionList().addAll(createHoldingTransaction(instrument_Trans.get(s)));
//                holdingDocument.calculateHoldingReport();
                stockHoldingCollectionList.add(holdingDocument);
            }
            //Steps:
            // Insert symbol and transaction in TransactionDocument
            return stockHoldingCollectionList;
        } else {
            // crypto processing
            //TODO: Update holding status
            List<CryptoHoldingCollection> cryptoHoldingCollectionList = new ArrayList<>();
            return cryptoHoldingCollectionList;
        }
    }

    private List<HoldingTransactions> createHoldingTransaction(List<InvestmentStockAccountStatement> investmentStockAccountStatements) {
        List<HoldingTransactions> holdingTrans = new ArrayList<>();
        for (InvestmentStockAccountStatement s : investmentStockAccountStatements) {
            HoldingTransactions ht = new HoldingTransactions();
            ht.setAccountStatementId(s.getId());
            ht.setSettleDate(s.getSettleDate());
            ht.setInstrument(s.getStock_instrument());
            ht.setTransCode(s.getTransCode());
            ht.setQuantity(s.getQuantity());
            ht.setRate(s.getRate());
            ht.setAmount(s.getAmount());
            ht.setDescriptions(s.getDescription());
            holdingTrans.add(ht);
        }
        return holdingTrans;
    }


    @Override
    public void updateNeededFlags(Boolean updateAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport) {
        this.setUpdateAccountStatementNeeded(updateAccountStatement);
        this.setUpdateAccountReportNeeded(updateAccountReport);
        this.setUpdateCashFlowReportNeeded(updateCashFlowReport);
    }

    public void updateStockStatement(List<InvestmentStockAccountStatement> statementList) {
        this.amountInvestment = BigDecimal.ZERO;
        this.cashInvestment = BigDecimal.ZERO;
        for (InvestmentStockAccountStatement trans_s : statementList) {
            switch (trans_s.getTransCode()) {
                case "Buy" -> {
                    log.info("Buy");
                    trans_s.setFee((trans_s.getQuantity().multiply(trans_s.getRate())).subtract(trans_s.getAmount().abs()));
                    this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                    log.info("desc: " + trans_s.getDescription() + " qty: " + trans_s.getQuantity() + " amount: " + trans_s.getAmount() + " fee: " + trans_s.getFee());
                }
                case "Sell" -> {
                    log.info("Sell");
                    trans_s.setFee((trans_s.getQuantity().multiply(trans_s.getRate())).subtract(trans_s.getAmount().abs()));
                    this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                    log.info("desc: " + trans_s.getDescription() + " qty: " + trans_s.getQuantity() + " amount: " + trans_s.getAmount() + " fee: " + trans_s.getFee());
                }
                case "CDIV" -> {
                    log.info("Dividend");
                    this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                }
                case "REC" -> {
                    log.info("Recived");
                    //Do Nothing
                }
                case "MISC" -> {
                    log.info("Price Corrections OR OCA");
                    this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                }
                case "OCA" -> {
                    log.info("OCA");
                    //Do Nothing
                }
                case "SLIP" -> {
                    log.info("Stock Lending");
                    this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                }
                case "BTC" -> {
                    log.info("For Options: BTC");
                    trans_s.setFee((trans_s.getAmount().abs()).subtract((trans_s.getQuantity().multiply(trans_s.getRate())).multiply(BigDecimal.valueOf(100))));
                    // reverse the condition
                    this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                    log.info("desc: " + trans_s.getDescription() + " qty: " + trans_s.getQuantity() + " amount: " + trans_s.getAmount() + " fee: " + trans_s.getFee());
                }
                case "OASGN" -> {
                    log.info("OASGN: For Options");
                    //Do Nothing
                }
                case "STO" -> {
                    log.info("STO: For Options");
                    trans_s.setFee(((trans_s.getQuantity().multiply(trans_s.getRate())).multiply(BigDecimal.valueOf(100))).subtract(trans_s.getAmount().abs()));
                    this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                    log.info("desc: " + trans_s.getDescription() + " qty: " + trans_s.getQuantity() + " amount: " + trans_s.getAmount() + " fee: " + trans_s.getFee());
                }
                case "OEXP" -> {
                    log.info("OEXP: For Options");
                    //Do Nothing 38582 20105+17391
                }
                case "ACH" -> {
                    log.info("ACH.");
                    if (trans_s.getDescription().contains("Deposit")) {
                        this.cashInvestment = this.cashInvestment.add(trans_s.getAmount());
                        this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                    } else if (trans_s.getDescription().contains("Withdrawal")) {
                        this.cashReturn = this.cashReturn.add(trans_s.getAmount().abs());
                        this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                    } else if (trans_s.getDescription().contains("Cancel")) {
                        this.cashInvestment = this.cashInvestment.subtract(trans_s.getAmount());
                    }
                }
                case "DTAX" -> {
                    log.info("Tax Withheld");
                    this.amountInvestment = this.amountInvestment.add(trans_s.getAmount());
                }
                default -> {
                    log.error("no Rule defined for : " + trans_s.getTransCode());
                }
            }
        }
        this.amountInvestment = this.amountInvestment.add(this.cashInvestment).subtract(this.cashReturn);
        log.info("Cash Investment: " + this.cashInvestment + " total investment: " + this.amountInvestment);
//        return statementList;
    }

    @Override
    public Query statementSortQuery() {
        if (AccountTypeEnum.STOCK.compareTo(this.getAccountType()) == 0) {
            Sort sort = Sort.by(Sort.Direction.ASC, "settleDate").and(Sort.by(Sort.Direction.ASC, "transCode"));
            Query query = new Query();
            query.with(sort);
            return query;

        } else {
            Sort sort = Sort.by(Sort.Direction.ASC, "transactionDate").and(Sort.by(Sort.Direction.ASC, "type"));
            Query query = new Query();
            query.with(sort);
            return query;
        }
    }

    @Override
    public void resetFields() {
        this.setUpdateAccountStatementNeeded(Boolean.FALSE);
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
}
