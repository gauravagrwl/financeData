package org.gauravagrwl.financeData.model.accountCollection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.enums.AccountTypeEnum;
import org.gauravagrwl.financeData.helper.enums.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Currency;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "account_collections")
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "institutionCategory", defaultImpl = InstitutionCategoryEnum.class, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BankAccountCollection.class, name = "BankAccount"),
        @JsonSubTypes.Type(value = InvestmentAccountCollection.class, name = "InvestmentAccount"),
        @JsonSubTypes.Type(value = LoanAccountCollection.class, name = "LoanAccount"),
        @JsonSubTypes.Type(value = AssetsAccountCollection.class, name = "AssetsAccount"),
})
@Component
public abstract class AccountCollection implements AccountOperation {

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


    // To store Account statement as from institute.
    private String accountTransactionCollectionName;

    //To transform Account Trans to Statement model.
    //Depends on indicator: updateAccountStatement if True
    private Boolean updateAccountStatementModelNeeded = Boolean.FALSE;
    private String accountStatementCollectionName;


    //Depends on indicator: updateAccountReport if True
    //EX: For assest -
    // For Investment - Holdings
    // For Banking - Cashflow
    private Boolean updateAccountReportNeeded = Boolean.FALSE;
    private String accountReportCollectionName;

    private Boolean updateCashFlowReportNeeded = Boolean.FALSE;


    // User profile who holds the account
    private String profileDocumentId;

    // Hard stop date no processing of any transaction after this date.
    private LocalDate hardStopDate;

    // Is this account still Active.
    private Boolean isActive = Boolean.TRUE;

    private AuditMetadata audit = new AuditMetadata();

    private Boolean balanceCalculated = Boolean.FALSE;

    @Version
    private Integer version;

    //Needed for data to upload. Name_accounttype
    private String profileType;

    @Transient
    private Boolean updateAccountStatement = Boolean.FALSE;

    private String accountDisplayName;

    private Boolean dataMatched = Boolean.FALSE;


}
