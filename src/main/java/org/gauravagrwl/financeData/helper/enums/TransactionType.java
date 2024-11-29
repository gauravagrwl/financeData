package org.gauravagrwl.financeData.helper.enums;

public enum TransactionType {

    //FOR ACCOUNT
    DEPOSIT,  // For any Cash deposit from Bank
    WITHDRAWAL,  // For any Cash withdrawal to Bank
    FAILED, // IF any bank Payment is failed.

    // FOR TRANSACTIONS
    BUY, // for Buy,
    OBTC, // FOR OPTION BTC
    SELL, // For Sell,
    OSTO, // FOR OPTION STO
    EARN, // SLIP, CDIV, REC Intrest, Stake amount
    EARN_REVERT,// SLIP, CDIV, REC Intrest, Stake amount reverted

    //FOR OTHERS
    CHARGES, // DTAX
    OTHERS,
    CANCEL, // For any transaction Cancel

    STAKE_DEPOSIT, // For stake_Deposit
    STAKE_WITHDRAWAL, // For stake_Deposit
    ;

}
