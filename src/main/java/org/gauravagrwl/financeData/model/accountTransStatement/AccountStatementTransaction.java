package org.gauravagrwl.financeData.model.accountTransStatement;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
public abstract class AccountStatementTransaction implements AccountStatementTransactionOperations {

    @MongoId
    private String id;

    private String accountDocumentId;

    // If duplicate Statement
    private Boolean duplicate = Boolean.FALSE;

    private Boolean reconciled = Boolean.FALSE;
    private AuditMetadata audit = new AuditMetadata();

    @Version
    private Integer version;

}
