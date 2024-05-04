package org.gauravagrwl.financeData.helper;

public enum TransactionKindEnum {
    /**
     * Buy: instrument(assets / coin) added under any transaction code.
     * Sell: instrument(assets / coin) removed or sold under any transaction code.
     * O_*: Option transactions
     * Earn: Div, REC, SLIP, MISC
     * Charge:DTAX,
     */

    Buy("Buy"),
    Sell("Sell"),

    O_EXP("OEXP"),

    O_STO("STO"),
    O_ASGN("OASGN"),

    O_BTC("BTC"),

    Deposit("Deposit"),

    Withdrawal("Withdrawal"),

    Cancel("Cancel"),

    Send("Send"),

    Earn("earn"),

    Charge("Charge"),

    Other("Other");
    private final String transaction_code;

    TransactionKindEnum(String transactionCode) {
        transaction_code = transactionCode;
    }
}
