package org.gauravagrwl.financeData.model.profileAccount.reportCollection.investment;

import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.ReportCollection;
import org.springframework.data.mongodb.core.index.Indexed;

public abstract class HoldingCollection extends ReportCollection implements HoldingOperations {

    @NotBlank
    @Setter
    @Indexed(unique = true, background = true)
    private String instrument;

}
