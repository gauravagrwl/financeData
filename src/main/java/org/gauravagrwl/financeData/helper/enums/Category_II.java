package org.gauravagrwl.financeData.helper.enums;

public enum Category_II {

    Inv_IN(Category_I.IN, "ROI"),
    Inv_OUT(Category_I.OUT, "Inv"),
    ;

    private String categoryI;

    private Category_I category_i;

    Category_II(Category_I category_i, String categoryI) {
        this.categoryI = categoryI;
        this.category_i = category_i;
    }
}
