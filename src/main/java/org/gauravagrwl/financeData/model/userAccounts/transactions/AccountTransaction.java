package org.gauravagrwl.financeData.model.userAccounts.transactions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.common.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
public abstract class AccountTransaction implements AccountTransactionOperations {


    @MongoId
    private String id;
    @Indexed
    private String userAccountId;
    private Boolean duplicateTransaction = Boolean.FALSE;
    @JsonIgnore
    private AuditMetadata audit = new AuditMetadata();
    @JsonIgnore
    @Version
    private Integer version;

}
