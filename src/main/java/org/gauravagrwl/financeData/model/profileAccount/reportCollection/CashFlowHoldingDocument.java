package org.gauravagrwl.financeData.model.profileAccount.reportCollection;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CashFlowHoldingDocument extends ReportCollection {

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

}
