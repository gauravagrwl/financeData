package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.helper.AccountTypeEnum;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.InvestmentCryptoAccountStatement;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.InvestmentStockAccountStatement;
import org.gauravagrwl.financeData.model.reports.AccountReportDocument;
import org.gauravagrwl.financeData.model.reports.CryptoHoldingDocument;
import org.gauravagrwl.financeData.model.reports.StockHoldingDocument;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public class InvestmentAccountDocument extends AccountDocument {

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
    public List<? extends AccountReportDocument> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList) {
        if (AccountTypeEnum.STOCK.compareTo(this.getAccountType()) == 0) {
            //Stock processing
            //TODO: Update holding status
            List<StockHoldingDocument> stockHoldingDocumentList = new ArrayList<>();
            //Steps:
            // Insert symbol and transaction in TransactionDocument
            return stockHoldingDocumentList;
        } else {
            // crypto processing
            //TODO: Update holding status
            List<CryptoHoldingDocument> cryptoHoldingDocumentList = new ArrayList<>();
            return cryptoHoldingDocumentList;
        }
    }

    @Override
    public void updateNeededStatementOrReports(Boolean updateAccountStatement, Boolean updateAccountReport) {
        this.setUpdateAccountStatement(updateAccountStatement);
        this.setUpdateAccountReport(updateAccountReport);
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
}
