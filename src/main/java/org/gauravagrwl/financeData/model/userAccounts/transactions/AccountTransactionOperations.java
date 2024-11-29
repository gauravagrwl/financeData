package org.gauravagrwl.financeData.model.userAccounts.transactions;

import org.gauravagrwl.financeData.model.userAccounts.statements.AccountStatement;

import java.util.List;

public interface AccountTransactionOperations {

    @Deprecated
    AccountStatement transformToStatement();

    List<AccountStatement> getAccountStatements();

    List<AccountStatement> transformToStatementList();

}

