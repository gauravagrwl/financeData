package org.gauravagrwl.financeData.model.profileAccount.accountStatement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class AccountStatementDocument {

    @MongoId
    private String id;

    private String accountDocumentId;

    @Indexed
    private Boolean reconciled = Boolean.FALSE;

    private Boolean duplicate = Boolean.FALSE;

    private AuditMetadata audit = new AuditMetadata();

    @Version
    private Integer version;
}
