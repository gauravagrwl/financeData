package org.gauravagrwl.financeData.model.accountReportsModel.banking;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.enums.Category_I;
import org.gauravagrwl.financeData.helper.enums.Category_II;
import org.gauravagrwl.financeData.helper.enums.Category_III;
import org.gauravagrwl.financeData.model.accountReportsModel.ReportCollection;
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
    private String type; // CashIn or CashOut
    private BigDecimal ammount = BigDecimal.ZERO;
    private Boolean reconciled = Boolean.FALSE;

    
    private Category_I category_i;
    private Category_II category_ii;
    private Category_III category_iii;
    private String category_iv;

    //statement id to handle other operations
    @NotBlank
    @Indexed(unique = true, background = true)
    private String accountStatementModelId;


    public void calculateReport() {
    }
}
