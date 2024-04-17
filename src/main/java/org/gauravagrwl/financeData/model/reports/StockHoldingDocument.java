package org.gauravagrwl.financeData.model.reports;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class StockHoldingDocument extends AccountReportDocument {
    @Getter
    @Setter
    @Indexed(unique = true, background = true)
    @NotBlank
    private String instrument;

    private BigDecimal quantity = BigDecimal.ZERO;

    private BigDecimal averageRate = BigDecimal.ZERO;

    private BigDecimal totalAmount = BigDecimal.ZERO;
    List<HoldingTransactions> holdingTransactionList;


    public void updateReport() {
        for (HoldingTransactions transactionDocument : holdingTransactionList) {
            switch (transactionDocument.getTransCode()) {
                case "Buy" -> {
                    log.info("Buy");
                    this.quantity = this.quantity.add(transactionDocument.getQuantity());
                    this.totalAmount = this.totalAmount.add(transactionDocument.getAmount());
                    this.averageRate = this.totalAmount.divide(this.quantity);
                }
                case "Sell" -> {
                    log.info("Sell");
                    this.quantity = this.quantity.subtract(transactionDocument.getQuantity());
                    this.totalAmount = this.totalAmount.subtract(transactionDocument.getAmount());
                    this.averageRate = this.totalAmount.divide(this.quantity);
                }
                case "CDIV" -> {
                    log.info("Dividend");
                    this.totalAmount = this.totalAmount.add(transactionDocument.getAmount());
                    this.averageRate = this.totalAmount.divide(this.quantity);
                }
                case "REC" -> {
                    log.info("Recived");
                    this.quantity = this.quantity.add(transactionDocument.getQuantity());
                    this.averageRate = this.totalAmount.divide(this.quantity);
                }
                case "MISC", "OCA" -> {
                    log.info("Price Corrections OR OCA");
                }
                case "SLIP" -> {
                    log.info("Stock Lending");
                    this.totalAmount = this.totalAmount.add(transactionDocument.getAmount());
                    this.averageRate = this.totalAmount.divide(this.quantity);
                }
                case "OEXP", "STO", "OASGN", "BTC" -> {
                    log.info("For Options");
                }
                case "ACH" -> {
                    log.info("ACH - Money transfer.");
                }
                case "DTAX" -> {
                    log.info("Tax Withheld");
                    this.totalAmount = this.totalAmount.add(transactionDocument.getAmount());
                    this.averageRate = this.totalAmount.divide(this.quantity);
                }
                default -> {
                    log.error("no Rule defined for : " + transactionDocument.getTransCode());
                }
            }
        }

    }
}
