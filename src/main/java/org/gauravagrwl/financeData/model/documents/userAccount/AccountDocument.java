package org.gauravagrwl.financeData.model.documents.userAccount;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.documents.base.AuditMetaData;
import org.gauravagrwl.financeData.utility.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.utility.InstitutionTypeEnum;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.util.Currency;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "institutionCategory", defaultImpl = InstitutionCategoryEnum.class, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BankAccountDocument.class, name = "BankAccountDocument"),
        @JsonSubTypes.Type(value = InvestmentAccountDocument.class, name = "InvestmentAccountDocument"),
        @JsonSubTypes.Type(value = LoanAccountDocument.class, name = "LoanAccountDocument"),
        @JsonSubTypes.Type(value = AssetsAccountDocument.class, name = "AssetsAccountDocument"),
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class AccountDocument {
    @MongoId
    private String Id;

    // Financial institute Name
    private String institutionName;

    // Financial institute Currency
    private Currency institutionCurrency;

    // Financial institute Type can be: BANKING, INVESTMENT, MARKET, LOAN
    private InstitutionCategoryEnum institutionCategory;

    // Financial institute Account Number must be unique
    @Indexed(unique = true, background = true)
    private String accountNumber;

    private InstitutionTypeEnum accountType;

    // User profile who holds the account
    private String profileDocumentId;

    // Is this account still Active.
    private Boolean isActive = Boolean.TRUE;

    //Set the CollectionName for statement
    private String collectionName;

    private AuditMetaData audit = new AuditMetaData();

    @Version
    private Integer version;

    // Indicator if respective Account Balance Is Calculated.
    private Boolean isBalanceCalculated = Boolean.FALSE;

    public abstract void calculate(BigDecimal amount);
}
