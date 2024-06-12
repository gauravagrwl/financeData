package org.gauravagrwl.financeData.model.userAccounts.transactions;

import org.gauravagrwl.financeData.model.userAccounts.statements.AccountStatement;

public interface AccountTransactionOperations {

    AccountStatement transformToStatement();

    AccountStatement getAccountStatement();

}

