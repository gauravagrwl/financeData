package org.gauravagrwl.financeData.model.userAccounts.statements;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.userAccounts.reports.ReportStatement;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "BankAccountStatement")
public class BankAccountStatement extends AccountStatement {

    private LocalDate transactionDate;
    private String description;
    private BigDecimal amount = BigDecimal.ZERO;
    //If Cr. or Dr.
    private String type;
    private BigDecimal transactionBalance = BigDecimal.ZERO;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'accountStatementId':?#{#self._id} }", collection = "CashFlowReportStatement")
    private ReportStatement reportStatement;

}
