package org.gauravagrwl.financeData.model.userAccounts.accounts;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.enums.AccountType;
import org.gauravagrwl.financeData.helper.enums.InstitutionCategory;
import org.gauravagrwl.financeData.model.common.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Currency;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "accounts")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "institutionCategory", defaultImpl = InstitutionCategory.class, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BankAccount.class, name = "BankAccount"),
        @JsonSubTypes.Type(value = InvestmentAccount.class, name = "InvestmentAccount"),
//        @JsonSubTypes.Type(value = LoanAccount.class, name = "LoanAccount"),
//        @JsonSubTypes.Type(value = AssetsAccount.class, name = "AssetsAccount"),
})
public abstract class UserAccount implements UserAccountOperations {
    @MongoId
    private String id;


    @NotBlank(message = "Institution Name is required. Ex: Chase / SBI")
    private String institutionName;

    //    @NotBlank(message = "Institution Currency is required. Ex: INR / USD")
    private Currency institutionCurrency;

    //    @NotBlank(message = "Institution Category is required. Ex: Banking, Investment, market  or Loan")
    private InstitutionCategory institutionCategory;

    @NotBlank(message = "Account Number is required.")
    @Indexed(unique = true, background = true)
    private String accountNumber;

    private String accountDisplayNumber;

    //    @NotBlank(message = "Account Type is required.")
    private AccountType accountType;

    // User profile who holds the account
    private String userProfileId;

    private Boolean isActive = Boolean.TRUE;

    //To distinguish each account.
    @Indexed(unique = true, background = true)
    private String accountDisplayName;

    // Needed for data to upload. Name_accounttype
    private String profileType;

    private String accountTransactionCollectionName;

    //Indicators
    private Boolean StartTransactionProcessing = Boolean.FALSE;

    @Version
    private Integer version;

    private AuditMetadata audit = new AuditMetadata();


}
