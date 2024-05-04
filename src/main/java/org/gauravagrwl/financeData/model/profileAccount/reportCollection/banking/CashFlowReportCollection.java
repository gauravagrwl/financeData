package org.gauravagrwl.financeData.model.profileAccount.reportCollection.banking;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.ReportCollection;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CashFlowReportCollection extends ReportCollection {

    private LocalDate transactionDate; // Date Of Transactions
    @Indexed
    private int year;
    private String description;
    private String transactionType; // CashIn or CashOut
    private BigDecimal cashIn;
    private BigDecimal cashOut;
    private Boolean reconciled;

    //statement id to handle other operations
    @NotBlank
    @Indexed(unique = true, background = true)
    private String accountStatementId;


    public void calculateReport() {

    }
}
