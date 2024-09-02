package org.gauravagrwl.financeData.model.userAccounts.statements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.common.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
public abstract class AccountStatement implements AccountStatementOperation {
    @MongoId
    private String id;

    @NotBlank
    @Indexed
    private String accountId;
    @NotBlank
    @Indexed(unique = true, background = true)
    private String accountTransactionId;
    private Boolean duplicate = Boolean.FALSE;
    @JsonIgnore
    private AuditMetadata audit = new AuditMetadata();
    @JsonIgnore
    @Version
    private Integer version;
}
