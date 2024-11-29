package org.gauravagrwl.financeData.helper.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionCategory {

    BUY("Buy Instrument or Coin", new String[]{"Buy", "BTC"}),
    SELL("Sell Instrument or Coin", new String[]{"Sell", "STO"}),

    DEPOSIT("Cash Deposits to Account", new String[]{"ACH"}),
    WITHDRAWAL("Cash Withdrawal to Account", new String[]{"ACH"}),
    FAILED("Cash Transaction is Failed to Account", new String[]{"ACH"}),

    EARN("Earn on instrument or coin in different form", new String[]{"SLIP", "CDIV", "REC"}),

    CHARGES("Charges on instrument or coin in different form", new String[]{"DTAX"}),

    OTHER("All other Transaction", new String[]{"OASGN", "OEXP"}),
    CANCEL("Transaction is Cancel", new String[]{"OCA"}),

    ;

//    MISC
//    crypto_earn_extra_interest_paid
//            crypto_earn_interest_paid
//    admin_wallet_credited
//            mco_stake_reward
//    referral_card_cashback
//            reimbursement
//    card_cashback_reverted
//            viban_deposit_precredit_repayment
//    crypto_earn_program_created
//            viban_deposit_precredit
//    rewards_platform_deposit_credited
//            reimbursement_reverted
//    crypto_earn_program_withdrawn
//            crypto_exchange
//    crypto_wallet_swap_credited
//            crypto_wallet_swap_debited
//    viban_purchase
//            dust_conversion_credited
//    dust_conversion_debited
//            referral_gift
//    lockup_lock

    private final String desc;
    private final String transaction[];

    TransactionCategory(String desc, String[] transaction) {
        this.desc = desc;
        this.transaction = transaction;
    }

    public TransactionCategory find(String transType) {
        if (transType == null) {
            throw new IllegalArgumentException();
        }
        for (TransactionCategory t : values()) {
            String[] transactionlist = t.getTransaction();
            if (Arrays.stream(transactionlist).anyMatch(transType::equalsIgnoreCase)) {
                return t;
            }
        }
        throw new IllegalArgumentException();
    }
}
