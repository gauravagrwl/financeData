package org.gauravagrwl.financeData.model.reports;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@Slf4j
public class StockHoldingDocument extends AccountReportDocument {
    @Indexed(unique = true, background = true)
    @NotBlank
    private String instrument;

    private BigDecimal totalQuantity = BigDecimal.ZERO;

    private BigDecimal averageRate = BigDecimal.ZERO;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private BigDecimal profitLoss = BigDecimal.ZERO;

    private Boolean need_correction = Boolean.FALSE;

    List<HoldingTransactions> holdingTransactionList = new ArrayList<>();


    public void calculateHoldingReport() {
        holdingTransactionList.sort(sortHoldingTransactionList);
        for (HoldingTransactions transactionDocument : holdingTransactionList) {
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
                    if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                        this.totalAmount = this.totalAmount.add(transactionDocument.getAmount().abs());
                        this.averageRate = this.totalAmount.divide(this.totalQuantity, RoundingMode.HALF_UP);
                    }

                }
                case "REC" -> {
                    log.info("Recived");
                    this.totalQuantity = this.totalQuantity.add(transactionDocument.getQuantity());
//                    this.averageRate = this.totalAmount.divide(this.quantity, RoundingMode.HALF_UP);
                }
                case "MISC", "OCA" -> {
                    log.info("Price Corrections OR OCA");
                }
                case "SLIP" -> {
                    log.info("Stock Lending");
                    this.totalAmount = this.totalAmount.add(transactionDocument.getAmount().abs());
                    this.averageRate = this.totalAmount.divide(this.totalQuantity, RoundingMode.HALF_UP);
                }
                case "OEXP" -> {
                    log.info("For Options: OEXP");
                }
                case "STO" -> {
                    log.info("For Options: STO");
                }
                case "OASGN" -> {
                    log.info("For Options: OASGN");
                }
                case "BTC" -> {
                    log.info("For Options: BTC");
                }
                case "ACH" -> {
                    log.info("ACH - Money transfer.");
                }
                case "DTAX" -> {
                    log.info("Tax Withheld");
                    this.totalAmount = this.totalAmount.add(transactionDocument.getAmount().abs());
                    this.averageRate = this.totalAmount.divide(this.totalQuantity, RoundingMode.HALF_UP);
                }
                default -> {
                    log.error("no Rule defined for : " + transactionDocument.getTransCode());
                }
            }
        }

    }

    public static Comparator<HoldingTransactions> sortHoldingTransactionList = Comparator
            .comparing(HoldingTransactions::getSettleDate).thenComparing(HoldingTransactions::getTransCode);
}
