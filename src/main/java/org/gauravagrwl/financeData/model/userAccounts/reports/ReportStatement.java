package org.gauravagrwl.financeData.model.userAccounts.reports;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.common.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
public abstract class ReportStatement {

    @MongoId
    private String id;

    @NotBlank
    private String accountId;


    @JsonIgnore
    private AuditMetadata audit = new AuditMetadata();
    @JsonIgnore
    @Version
    private Integer version;
}
