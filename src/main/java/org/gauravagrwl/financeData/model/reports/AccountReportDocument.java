package org.gauravagrwl.financeData.model.reports;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.MongoId;

public abstract class AccountReportDocument {
    @MongoId
    private String id;

    @NotBlank
    private String accountDocumentId;


}
