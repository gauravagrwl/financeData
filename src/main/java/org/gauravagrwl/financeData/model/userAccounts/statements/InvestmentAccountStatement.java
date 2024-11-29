package org.gauravagrwl.financeData.model.userAccounts.statements;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.FinanceAppHelper;
import org.gauravagrwl.financeData.helper.enums.TransactionType;
import org.gauravagrwl.financeData.model.userAccounts.reports.ReportStatement;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

@Getter
@Setter
@Document(collection = "InvestmentAccountStatement")
public class InvestmentAccountStatement extends AccountStatement {

    /*
    Date of the transactions
     */
    private ZonedDateTime transactionDate;
    /*
    Instrument Or Crypto Coin
     */
    private String instrument;
    /*
    Transaction Descriptions
     */
    private String description;
    /*
    Transaction Type
     */
    private TransactionType transactionType;
    /*
    Instrument Quantity or Crypto Coin amount / quantity
     */
    private BigDecimal quantity;
    /*
    Instrument or Crypto purchase price
     */
    private BigDecimal rate = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    /*
    Total amount for purchase
     */
    private BigDecimal amount = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    /*
    Fee incurred.
     */
    private BigDecimal fee = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);


    @ReadOnlyProperty
    @DocumentReference(lookup = "{'accountStatementId':?#{#self._id} }", collection = "HoldingReportStatement")
    private ReportStatement reportStatement;

    @Override
    public String toString() {
        return "InvestmentAccountStatement{" +
                "transactionDate=" + transactionDate +
                ", instrument='" + instrument + '\'' +
                ", description='" + description + '\'' +
                ", transactionType=" + transactionType +
                ", quantity=" + quantity +
                ", price=" + rate +
                ", amount=" + amount +
                ", fee=" + fee +
                ", reportStatement=" + reportStatement +
                '}';
    }


}
