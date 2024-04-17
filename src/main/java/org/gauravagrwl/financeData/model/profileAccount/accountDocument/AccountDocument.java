package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.AccountTypeEnum;
import org.gauravagrwl.financeData.helper.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.stereotype.Component;

import java.util.Currency;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "account_collections")
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "institutionCategory", defaultImpl = InstitutionCategoryEnum.class, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BankAccountDocument.class, name = "BankAccount"),
        @JsonSubTypes.Type(value = InvestmentAccountDocument.class, name = "InvestmentAccount"),
        @JsonSubTypes.Type(value = LoanAccountDocument.class, name = "LoanAccount"),
        @JsonSubTypes.Type(value = AssetsAccountDocument.class, name = "AssetsAccount"),
})
@Component
public abstract class AccountDocument implements UserAccountOperation {

    @MongoId
    private String id;

    // Financial institute Name
    @NotBlank(message = "Institution Name is required. Ex: Chase / SBI")
    private String institutionName;

    // Financial institute Currency
    // @NotBlank(message = "Institution Currency is required. Ex: INR / USD")
    private Currency institutionCurrency;

    // Financial institute Type can be: BANKING, INVESTMENT, MARKET, LOAN
    // @NotBlank(message = "Institution Category is required.")
    private InstitutionCategoryEnum institutionCategory;

    // Financial institute Account Number must be unique
    @NotBlank(message = "Account Number is required.")
    @Indexed(unique = true, background = true)
    private String accountNumber;

    // @NotBlank(message = "Account Type is required.")
    private AccountTypeEnum accountType;

    private String accountStatementCollectionName;

    private String accountReportCollectionName;

    // User profile who holds the account
    private String profileDocumentId;

    // Is this account still Active.
    private Boolean isActive = Boolean.TRUE;

    private AuditMetadata audit = new AuditMetadata();

    private Boolean balanceCalculated = Boolean.FALSE;

    @Version
    private Integer version;

    //Needed for data to upload. Name_accounttype
    private String csvProfile;

    private Boolean updateAccountStatement = Boolean.FALSE;
    private Boolean updateAccountReport = Boolean.FALSE;


}
