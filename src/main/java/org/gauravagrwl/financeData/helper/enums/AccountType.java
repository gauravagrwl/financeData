package org.gauravagrwl.financeData.helper.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum AccountType {
    CHECKING(InstitutionCategory.BANKING, "CHK", "101"),
    SAVING(InstitutionCategory.BANKING, "SAV", "102"),
    DEPOSIT(InstitutionCategory.BANKING, "DEP", "103"),
    PPF(InstitutionCategory.BANKING, "PPF", "104"),
    CREDIT(InstitutionCategory.BANKING, "CRE", "105"),

    STOCK(InstitutionCategory.INVESTMENT, "STOCK", "201"),
    CRYPTO(InstitutionCategory.INVESTMENT, "CRYPTO", "202"),

    PROPERTY(InstitutionCategory.ASSETS, "PROPERTY", "301"),

    LOAN(InstitutionCategory.LOAN, "LOAN", "401"),
    ;
    private final InstitutionCategory accountCategory;
    private final String accountTypeName, accountTypeCode;

    AccountType(InstitutionCategory accountCategory, String accountTypeName, String accountTypeCode) {
        this.accountCategory = accountCategory;
        this.accountTypeName = accountTypeName;
        this.accountTypeCode = accountTypeCode;
    }

    AccountType(InstitutionCategory accountCategory, String accountTypeName, Integer accountTypeCode) {
        this(accountCategory, accountTypeName, accountTypeCode.toString());
    }

    @JsonCreator
    public static AccountType find(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        for (AccountType c : values()) {
            if (value.equalsIgnoreCase(c.name())) {
                return c;
            }
        }
        throw new IllegalArgumentException();
    }
}
