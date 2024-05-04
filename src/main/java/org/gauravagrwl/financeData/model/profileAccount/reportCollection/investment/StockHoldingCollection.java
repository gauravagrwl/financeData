package org.gauravagrwl.financeData.model.profileAccount.reportCollection.investment;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;
import org.gauravagrwl.financeData.model.statementModel.StockInvestmentAccountStatementModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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

    List<AccountStatementTransaction> holdingTransactionList = new ArrayList<>();


    private void resetFields() {
        totalQuantity = BigDecimal.ZERO;
        averageRate = BigDecimal.ZERO;
        totalAmount = BigDecimal.ZERO;
        optionAmount = BigDecimal.ZERO;
        divAmount = BigDecimal.ZERO;
        profitLoss = BigDecimal.ZERO;
        need_correction = Boolean.FALSE;
    }


    @Override
    public void calculateHolding(StatementModel accountStatement) {
        StockInvestmentAccountStatementModel s = (StockInvestmentAccountStatementModel) accountStatement;
        switch (s.getC_trans_code()) {
            case Buy -> {
                totalQuantity = totalQuantity.add(s.getC_quantity());
                totalAmount = totalAmount.add(s.getC_amount().abs());
                averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
            }
            case Sell -> {
                if (totalQuantity.compareTo(s.getC_quantity()) == 0) {
                    // Complete Sold-out
                    profitLoss = profitLoss.add(s.getC_amount().subtract(totalAmount)).setScale(2, RoundingMode.UP);
                    averageRate = totalQuantity = totalAmount = BigDecimal.ZERO;
                } else if (totalQuantity.compareTo(s.getC_quantity()) == 1) {
                    // calculate profilt on that partial sold.
                    profitLoss = profitLoss.add(s.getC_amount().subtract(s.getC_quantity().multiply(averageRate))).setScale(2, RoundingMode.UP);
                    // Partial sold
                    totalQuantity = totalQuantity.subtract(s.getC_quantity());
                    totalAmount = totalQuantity.multiply(averageRate);
                } else {
                    need_correction = Boolean.TRUE;
                }
            }
            case Earn -> {
                totalAmount = totalAmount.add(s.getC_amount().abs());
                totalQuantity = totalQuantity.add(s.getC_quantity());
                if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                    averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
                }
            }
            case Charge -> {
                totalAmount = totalAmount.subtract(s.getC_amount().abs());
                if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                    averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
                }
            }
            case O_STO -> {
                optionAmount = optionAmount.add(s.getC_amount());
            }
            case O_BTC -> {
                optionAmount = optionAmount.subtract(s.getC_amount().abs());
            }
            default -> {
                log.info("No rule define for statement: " + s);
            }

        }
    }
}

//        if (s.getC_instrument().equalsIgnoreCase("SOFI")) {
//            log.info("----------->" + s.toString());
//        }
//        HoldingTransactions transactionDocument = createHoldingTransaction(s);
//        this.holdingTransactionList.add(transactionDocument);
//        try {
//            switch (transactionDocument.getTransCode()) {
//                case "MISC", "OCA" -> {
//                    log.info("Price Corrections OR OCA. No Action Needed.");
//
//                }
//                case "OASGN" -> {
//                    log.info("OASGN: No action is needed. Given Option is assigned : " + transactionDocument.getDescriptions());
//                }
//                }
//                default -> {
//                    log.error("no Rule defined for : " + transactionDocument.getTransCode());
//                }
//            }
//        } catch (Exception e) {
//            log.error(" Unable to process Transaction: " + transactionDocument.getInstrument() + " " + transactionDocument.getTransCode() + " " + transactionDocument.getDescriptions() + " with error message: " + e.getMessage());
//        }
//    }
//}

