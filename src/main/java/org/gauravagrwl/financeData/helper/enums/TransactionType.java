package org.gauravagrwl.financeData.helper.enums;

public enum TransactionType {
    Buy, // for Buy
    Sell, // For Sell
    Deposit,  // For any Cash deposit from Bank
    Withdrawal,  // For any Cash withdrawal to Bank

    Failed, // IF any bank Payment is failed.
    Cancel, // For any Cancel
    OBTC, // Stock Buy to Close Option

    OSTO, // Stock Sell To Open Option

    Earn, // For any amount earn in form of Dividends
    OEXP, // Option Expired

    OASGN, // Option Assigned

    TAX, // Tax Transactions

    Charge, // Any other charges

    ;

}
