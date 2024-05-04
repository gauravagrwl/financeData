package org.gauravagrwl.financeData.model.profileAccount.accountCollection;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.investment.cryptoInvestment.CoinbaseAccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.investment.cryptoInvestment.CryptoAppAccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.investment.stockInvestment.RobinhoodStockAccountStatementTransaction;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;
import org.gauravagrwl.financeData.model.statementModel.StockInvestmentAccountStatementModel;
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
                        Criteria.where("timestamp").is(statement.getTimestamp())
                                .and("transaction_description").is(statement.getTransaction_description())
                                .and("crypto_currency").is(statement.getCrypto_currency())
                                .and("amount").is(statement.getAmount())
                                .and("to_Currency").is(statement.getTo_Currency())
                                .and("to_Amount").is(statement.getTo_Amount())
                                .and("native_Currency").is(statement.getNative_Amount())
                                .and("native_Amount").is(statement.getNative_Amount())
                                .and("native_Amount_USD").is(statement.getNative_Amount_USD())
                                .and("transaction_kind").is(statement.getTransaction_kind())
                                .and("transaction_hash").is(statement.getTransaction_hash()));
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
        this.setUpdateAccountAppStatementNeeded(updateAppAccountStatement);
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
                    log.info(s.toString());
                    if (s.getC_amount().compareTo(BigDecimal.ZERO) != 0)
                        amountInvestment = amountInvestment.add(s.getC_amount());
                }
                case Sell, O_STO, Charge -> {
                    log.info(s.toString());
                    if (s.getC_amount().compareTo(BigDecimal.ZERO) != 0)
                        amountInvestment = amountInvestment.subtract(s.getC_amount());
                }
                case Deposit -> {
                    log.info(s.toString());
                    cashInvestment = cashInvestment.add(s.getC_amount());
                }
                case O_EXP, O_ASGN -> {
                    log.info(s.toString());
                }
                case Cancel -> {
                    log.info(s.toString());
                    cashInvestment = cashInvestment.subtract(s.getC_amount());
                }
                case Withdrawal -> {
                    log.info(s.toString());
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
    public Query statementSortQuery() {
        Sort sort = null;
        Query query = new Query();
        switch (getProfileType()) {
            case "Robinhood_STOCK" -> {
                sort = Sort.by(Sort.Direction.ASC, "s_settle_Date").and(Sort.by(Sort.Direction.ASC, "s_trans_Code"));
            }
        }
        return (sort != null) ? query.with(sort) : query;
    }

    @Override
    public void resetFields() {
        this.setUpdateAccountAppStatementNeeded(Boolean.FALSE);
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
