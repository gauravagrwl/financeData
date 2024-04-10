package org.gauravagrwl.financeData.utility;

public enum InstitutionCategoryEnum {
    BANKING("BankAccount", "1"),
    INVESTMENT("InvestmentAccount", "2"),
    ASSETS("AssetsAccount", "3"),
    LOAN("LoanAccount", "4"),
    ;

    private String categoryName;
    private String categoryCode;

    private InstitutionCategoryEnum(String categoryName, String categoryCode) {
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
    }

    private InstitutionCategoryEnum(String categoryName, Integer categoryCode) {
        this(categoryName, categoryCode.toString());
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }
}