package org.gauravagrwl.financeData.helper;

import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;

import java.util.Currency;

public final class FinanceAppHelper {

    public static final int cryptoScale = 10;
    public static final int currencyScale = 2;
    public static final Currency currencyUSD = Currency.getInstance("USD");
    public static final Currency currencyINR = Currency.getInstance("USD");

    public static String getAccountDisplayName(UserAccount userAccount) {
        return StringUtils.join(getAccountProfileName(userAccount), "_", StringUtils.right(userAccount.getAccountNumber(), 3));
    }

    public static String getAccountProfileName(UserAccount userAccount) {
        return StringUtils.join(userAccount.getInstitutionName(), "_", userAccount.getAccountType().getAccountTypeName());
    }

    public static String getAccountTransactionCollectionName(UserAccount userAccount) {
        return StringUtils.join(getAccountDisplayName(userAccount), "_", "Transactions");
    }

    public static String getAccountDisplayNumber(String accountNumber) {
        return StringUtils.join("XXXX", StringUtils.right(accountNumber, 3));
    }
}
