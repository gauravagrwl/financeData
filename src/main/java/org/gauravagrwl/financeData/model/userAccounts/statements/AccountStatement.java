package org.gauravagrwl.financeData.model.userAccounts.statements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.common.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Getter
@Setter
public abstract class AccountStatement implements AccountStatementOperation {
    @MongoId
    private String id;

    @NotBlank
    @Indexed
    private String accountId;

    @NotEmpty
    @Indexed(background = true)
    List<String> accountTransactionIds = new ArrayList<>();


    private Boolean duplicate = Boolean.FALSE;

    private Currency currency;

    @JsonIgnore
    private AuditMetadata audit = new AuditMetadata();

    @JsonIgnore
    @Version
    private Integer version;

    @Override
    public String toString() {
        return "AccountStatement{" +
                "accountId='" + accountId + '\'' +
                ", accountTransactionId='" + accountTransactionIds + '\'' +
                ", duplicate=" + duplicate +
                ", id='" + id + '\'' +
                '}';
    }
}
