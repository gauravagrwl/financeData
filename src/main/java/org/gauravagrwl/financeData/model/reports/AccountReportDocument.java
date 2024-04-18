package org.gauravagrwl.financeData.model.reports;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.MongoId;

public abstract class AccountReportDocument {
    @MongoId
    @Getter
    private String id;

    @NotBlank
    @Getter
    @Setter
    private String accountDocumentId;


}
