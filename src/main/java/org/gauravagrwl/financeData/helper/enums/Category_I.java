package org.gauravagrwl.financeData.helper.enums;

public enum Category_I {
    IN("Cash In"),
    OUT("Cash Out"),
    ;

    private String categoryI;

    private Category_I(String categoryI) {
        this.categoryI = categoryI;
    }

    public String getCategoryI() {
        return categoryI;
    }
}
