package org.gauravagrwl.financeData.model.profileAccount.reportCollection;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.MongoId;

public abstract class ReportCollection {

    @MongoId
    @Getter
    private String id;

    @NotBlank
    @Getter
    @Setter
    private String accountDocumentId;

    private AuditMetadata audit = new AuditMetadata();
    @Version
    private Integer version;

}
