package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.opencsv.bean.MappingStrategy;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.AccountTypeEnum;
import org.gauravagrwl.financeData.helper.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

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
public abstract class AccountDocument {

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

    // User profile who holds the account
    private String profileDocumentId;

    // Is this account still Active.
    private Boolean isActive = Boolean.TRUE;

    private AuditMetadata audit = new AuditMetadata();

    @Version
    private Integer version;

    //Needed for data to upload. Name_accounttype
    private String csvProfile;

    // Indicator if respective Account Balance Is Calculated.
    private Boolean balanceCalculatedFlag = Boolean.FALSE;

    public abstract MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(String mappingProfile);

    public abstract Update getUpdateBalanceUpdateQuery(BigDecimal amount);

    public Update getBalanceCalculatedFlagQuery(Boolean flag) {
        return Update.update("isBalanceCalculated", flag);
    }

    public abstract Query findDuplicateRecordQuery(AccountStatementDocument statementDocument);

    public abstract BigDecimal getAccountStatementBalance();

    /**
     * Set @param balanceCalculatedFlag which is required to calculate the account statement balance.
     * True: Not needed. (Balance is calculated.)
     * False: Needed (Balance is calculated.)
     *
     * @param flag
     */
    public abstract void balanceCalculationNeeded();

    public abstract List<? extends AccountStatementDocument> calculateAccountBalance(List<? extends AccountStatementDocument> statementDocumentList);

}
