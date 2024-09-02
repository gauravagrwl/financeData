package org.gauravagrwl.financeData.model.userAccounts.statements;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.enums.TransactionType;
import org.gauravagrwl.financeData.model.userAccounts.reports.ReportStatement;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Document(collection = "InvestmentAccountStatement")
public class InvestmentAccountStatement extends AccountStatement {

    private ZonedDateTime transactionDate;
    private String instrument;

    private String description;
    private TransactionType transactionType;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    private BigDecimal fee;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'accountStatementId':?#{#self._id} }", collection = "HoldingReportStatement")
    private ReportStatement reportStatement;


}
