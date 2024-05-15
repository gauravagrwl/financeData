package org.gauravagrwl.financeData.helper.enums;

public enum Category_III {

    INV_Crypto(Category_II.Inv_OUT, "Crypto"),

    ROI_Crypto(Category_II.Inv_IN, "Crypto"),


    ;


    private Category_II category_ii;

    private String categoryIII;

    Category_III(Category_II category_ii, String categoryIII) {
        this.category_ii = category_ii;
        this.categoryIII = categoryIII;
    }
}
