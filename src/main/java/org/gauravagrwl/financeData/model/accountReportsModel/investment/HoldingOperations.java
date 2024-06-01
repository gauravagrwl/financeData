package org.gauravagrwl.financeData.model.accountReportsModel.investment;

import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;

import java.util.List;

public interface HoldingOperations {

    void calculateHolding(StatementModel accountStatement);

    List<AccountStatementTransaction> getHoldingTransactionList();


}
