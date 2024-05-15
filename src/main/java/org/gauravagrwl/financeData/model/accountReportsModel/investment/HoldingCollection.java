package org.gauravagrwl.financeData.model.accountReportsModel.investment;

import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.gauravagrwl.financeData.model.accountReportsModel.ReportCollection;
import org.springframework.data.mongodb.core.index.Indexed;

public abstract class HoldingCollection extends ReportCollection implements HoldingOperations {

    @NotBlank
    @Setter
    @Indexed(unique = true, background = true)
    private String instrument;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HoldingCollection{");
        sb.append("instrument='").append(instrument).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
