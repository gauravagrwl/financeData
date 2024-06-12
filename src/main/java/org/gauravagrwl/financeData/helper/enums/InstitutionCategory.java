package org.gauravagrwl.financeData.helper.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum InstitutionCategory {
    BANKING("BankAccount", "1"),
    INVESTMENT("InvestmentAccount", "2"),
    ASSETS("AssetsAccount", "3"),
    LOAN("LoanAccount", "4"),
    ;
    private final String categoryName;
    private final
    String categoryCode;

    InstitutionCategory(String categoryName, String categoryCode) {
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
    }

    InstitutionCategory(String categoryName, Integer categoryCode) {
        this(categoryName, categoryCode.toString());
    }

    @JsonCreator
    public static InstitutionCategory find(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        for (InstitutionCategory c : values()) {
            if (value.equalsIgnoreCase(c.getCategoryName())) {
                return c;
            }
        }
        throw new IllegalArgumentException();
    }

}
