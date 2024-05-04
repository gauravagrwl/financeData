package org.gauravagrwl.financeData.model.statementModel;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
public abstract class StatementModel {
    @MongoId
    private String id;

    private String accountStatementId;

    private String accountId;

    // If duplicate Statement
    private Boolean duplicate = Boolean.FALSE;

    // If statement is reconciled: update in cash_flow or holding calculations
    private Boolean reconciled = Boolean.FALSE;

    private AuditMetadata audit = new AuditMetadata();

    @Version
    private Integer version;
}
