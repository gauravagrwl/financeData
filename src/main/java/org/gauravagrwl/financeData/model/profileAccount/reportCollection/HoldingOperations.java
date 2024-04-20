package org.gauravagrwl.financeData.model.profileAccount.reportCollection;

import org.gauravagrwl.financeData.model.profileAccount.statementCollection.AccountStatementDocument;

public interface HoldingOperations {

    void updateHoldingTransactionList(AccountStatementDocument accountStatement);

    void calculateHolding();

    void calculateHolding_PartTwo(AccountStatementDocument accountStatement);


}
