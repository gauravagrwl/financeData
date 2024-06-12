package org.gauravagrwl.financeData.model.userAccounts.reports;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "CashFlowReportStatement")
public class CashFlowReportStatement extends ReportStatement {

    private LocalDate transactionDate;
    @Indexed
    private int year;
    private String description;
    private String type;
    private BigDecimal amount = BigDecimal.ZERO;
    private String notes;
    private String levelOne;
    private String levelTwo;
    private String levelThree;
    private String levelFour;

    @NotBlank
    private String accountStatementId;


}
