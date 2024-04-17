package org.gauravagrwl.financeData.model.reports;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HoldingTransactions {

    //statement id to handle other operations
    @NotBlank
    @Indexed(unique = true, background = true)
    private String accountStatementId;

    private LocalDate settleDate;

    private String instrument;

    private String transCode;

    private BigDecimal quantity;

    private BigDecimal rate;

    private BigDecimal amount;

    private String descriptions;

}
