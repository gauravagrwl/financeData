package org.gauravagrwl.financeData.model.profileAccount.reportCollection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.InvestmentStockAccountStatement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Slf4j
public class StockHoldingCollection extends HoldingCollection {


    private BigDecimal totalQuantity = BigDecimal.ZERO;

    private BigDecimal averageRate = BigDecimal.ZERO;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private BigDecimal optionAmount = BigDecimal.ZERO;

    private BigDecimal divAmount = BigDecimal.ZERO;

    private BigDecimal profitLoss = BigDecimal.ZERO;

    private Boolean need_correction = Boolean.FALSE;

    List<HoldingTransactions> holdingTransactionList = new ArrayList<>();


    public static Comparator<HoldingTransactions> sortHoldingTransactionList = Comparator
            .comparing(HoldingTransactions::getSettleDate).thenComparing(HoldingTransactions::getTransCode);


    @Override
    public void calculateHolding() {
        resetFields();
        holdingTransactionList.sort(sortHoldingTransactionList);
        for (HoldingTransactions transactionDocument : holdingTransactionList) {
            if ("FRC".equalsIgnoreCase(transactionDocument.getInstrument())) {
                log.info(transactionDocument.getInstrument());
            }
            try {

                switch (transactionDocument.getTransCode()) {
                    case "Buy" -> {
                        log.info("Buy");
                        totalQuantity = totalQuantity.add(transactionDocument.getQuantity());
                        totalAmount = totalAmount.add(transactionDocument.getAmount().abs());
                        averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
                    }
                    case "Sell" -> {
                        log.info("Sell");
                        if (totalQuantity.compareTo(transactionDocument.getQuantity()) == 0) {
                            // Complete Sold-out
                            profitLoss = profitLoss.add(transactionDocument.getAmount().subtract(totalAmount));
                            averageRate = totalQuantity = totalAmount = BigDecimal.ZERO;
                        } else if (totalQuantity.compareTo(transactionDocument.getQuantity()) == 1) {
                            // calculate profilt on that partial sold.
                            profitLoss = profitLoss.add(transactionDocument.getAmount().subtract(transactionDocument.getQuantity().multiply(averageRate)));
                            // Partial sold
                            totalQuantity = totalQuantity.subtract(transactionDocument.getQuantity());
                            totalAmount = totalQuantity.multiply(averageRate);
                        } else {
                            need_correction = Boolean.TRUE;
                        }

                    }
                    case "CDIV" -> {
                        log.info("Dividend");
                        divAmount = divAmount.add(transactionDocument.getAmount());

                    }
                    case "REC" -> {
                        log.info("REC: stock instrument received as gift.");
                        this.totalQuantity = this.totalQuantity.add(transactionDocument.getQuantity());
                    }
                    case "MISC", "OCA" -> {
                        log.info("Price Corrections OR OCA. No Action Needed.");

                    }
                    case "SLIP" -> {
                        log.info("Stock Lending");
                        if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                            this.totalAmount = this.totalAmount.add(transactionDocument.getAmount().abs());
                            this.averageRate = this.totalAmount.divide(this.totalQuantity, RoundingMode.HALF_UP);
                        }
                    }
                    case "OEXP" -> {
                        log.info("OEXP: No action is needed. Given Option is expired : " + transactionDocument.getDescriptions());
                    }
                    case "STO" -> {
                        log.info("STO: Option Sell to Open: " + transactionDocument.getDescriptions());
                        optionAmount = optionAmount.add(transactionDocument.getAmount());

                    }
                    case "OASGN" -> {
                        log.info("OASGN: No action is needed. Given Option is assigned : " + transactionDocument.getDescriptions());
                    }
                    case "BTC" -> {
                        log.info("BTC: Option Buy To Close: " + transactionDocument.getDescriptions());
                        optionAmount = optionAmount.subtract(transactionDocument.getAmount().abs());
                    }
                    case "ACH" -> {
                        log.info("ACH - Money transfer.");
                    }
                    case "DTAX" -> {
                        log.info("Tax Withheld");
                        if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                            this.totalAmount = this.totalAmount.subtract(transactionDocument.getAmount().abs());
                            this.averageRate = this.totalAmount.divide(this.totalQuantity, RoundingMode.HALF_UP);
                        }
                    }
                    default -> {
                        log.error("no Rule defined for : " + transactionDocument.getTransCode());
                    }
                }
            } catch (Exception e) {
                log.error(" Unable to process Transaction: " + transactionDocument.getInstrument() + " " + transactionDocument.getTransCode() + " " + transactionDocument.getDescriptions() + " with error message: " + e.getMessage());
            }
        }
    }

    @Override
    public void updateHoldingTransactionList(AccountStatementDocument accountStatement) {
        InvestmentStockAccountStatement s = (InvestmentStockAccountStatement) accountStatement;
        this.holdingTransactionList.add(createHoldingTransaction(s));
    }


    private void resetFields() {
        totalQuantity = BigDecimal.ZERO;
        averageRate = BigDecimal.ZERO;
        totalAmount = BigDecimal.ZERO;
        optionAmount = BigDecimal.ZERO;
        divAmount = BigDecimal.ZERO;
        profitLoss = BigDecimal.ZERO;
        need_correction = Boolean.FALSE;
    }

    private HoldingTransactions createHoldingTransaction(InvestmentStockAccountStatement s) {
        HoldingTransactions ht = new HoldingTransactions();
        ht.setAccountStatementId(s.getId());
        ht.setSettleDate(s.getSettleDate());
        ht.setInstrument(s.getStock_instrument());
        ht.setTransCode(s.getTransCode());
        ht.setQuantity(s.getQuantity());
        ht.setRate(s.getRate());
        ht.setAmount(s.getAmount());
        ht.setDescriptions(s.getDescription());
        return ht;
    }

    @Override
    public void calculateHolding_PartTwo(AccountStatementDocument accountStatement) {
        InvestmentStockAccountStatement s = (InvestmentStockAccountStatement) accountStatement;
        if (s.getStock_instrument().equalsIgnoreCase("SOFI")) {
            log.info("----------->" + s.getSettleDate() + " " + s.getTransCode() + " " + s.getDescription());
        }
        HoldingTransactions transactionDocument = createHoldingTransaction(s);
        this.holdingTransactionList.add(transactionDocument);
        try {
            switch (transactionDocument.getTransCode()) {
                case "Buy" -> {
                    log.info("Buy");
                    totalQuantity = totalQuantity.add(transactionDocument.getQuantity());
                    totalAmount = totalAmount.add(transactionDocument.getAmount().abs());
                    averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
                }
                case "Sell" -> {
                    log.info("Sell");
                    if (totalQuantity.compareTo(transactionDocument.getQuantity()) == 0) {
                        // Complete Sold-out
                        profitLoss = profitLoss.add(transactionDocument.getAmount().subtract(totalAmount));
                        averageRate = totalQuantity = totalAmount = BigDecimal.ZERO;
                    } else if (totalQuantity.compareTo(transactionDocument.getQuantity()) == 1) {
                        // calculate profilt on that partial sold.
                        profitLoss = profitLoss.add(transactionDocument.getAmount().subtract(transactionDocument.getQuantity().multiply(averageRate)));
                        // Partial sold
                        totalQuantity = totalQuantity.subtract(transactionDocument.getQuantity());
                        totalAmount = totalQuantity.multiply(averageRate);
                    } else {
                        need_correction = Boolean.TRUE;
                    }

                }
                case "CDIV" -> {
                    log.info("Dividend");
                    divAmount = divAmount.add(transactionDocument.getAmount());

                }
                case "REC" -> {
                    log.info("REC: stock instrument received as gift.");
                    this.totalQuantity = this.totalQuantity.add(transactionDocument.getQuantity());
                }
                case "MISC", "OCA" -> {
                    log.info("Price Corrections OR OCA. No Action Needed.");

                }
                case "SLIP" -> {
                    log.info("Stock Lending");
                    if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                        this.totalAmount = this.totalAmount.add(transactionDocument.getAmount().abs());
                        this.averageRate = this.totalAmount.divide(this.totalQuantity, RoundingMode.HALF_UP);
                    }
                }
                case "OEXP" -> {
                    log.info("OEXP: No action is needed. Given Option is expired : " + transactionDocument.getDescriptions());
                }
                case "STO" -> {
                    log.info("STO: Option Sell to Open: " + transactionDocument.getDescriptions());
                    optionAmount = optionAmount.add(transactionDocument.getAmount());

                }
                case "OASGN" -> {
                    log.info("OASGN: No action is needed. Given Option is assigned : " + transactionDocument.getDescriptions());
                }
                case "BTC" -> {
                    log.info("BTC: Option Buy To Close: " + transactionDocument.getDescriptions());
                    optionAmount = optionAmount.subtract(transactionDocument.getAmount().abs());
                }
                case "ACH" -> {
                    log.info("ACH - Money transfer.");
                }
                case "DTAX" -> {
                    log.info("Tax Withheld");
                    if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                        this.totalAmount = this.totalAmount.subtract(transactionDocument.getAmount().abs());
                        this.averageRate = this.totalAmount.divide(this.totalQuantity, RoundingMode.HALF_UP);
                    }
                }
                default -> {
                    log.error("no Rule defined for : " + transactionDocument.getTransCode());
                }
            }
        } catch (Exception e) {
            log.error(" Unable to process Transaction: " + transactionDocument.getInstrument() + " " + transactionDocument.getTransCode() + " " + transactionDocument.getDescriptions() + " with error message: " + e.getMessage());
        }
    }
}

