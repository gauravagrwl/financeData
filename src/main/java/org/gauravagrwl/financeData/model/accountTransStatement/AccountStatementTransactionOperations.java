package org.gauravagrwl.financeData.model.accountTransStatement;

import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;

import java.util.List;

public interface AccountStatementTransactionOperations {

    //Need to return list to handle convert scenarios...
    List<StatementModel> updateAccountStatement(AccountCollection accountCollection);

}
