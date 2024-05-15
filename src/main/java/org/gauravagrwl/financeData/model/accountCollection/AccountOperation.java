package org.gauravagrwl.financeData.model.accountCollection;

import com.opencsv.bean.MappingStrategy;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;

public interface AccountOperation {

    // List of abstract method:

    // Helps to find the duplicate Query -- IMP
    Query findDuplicateRecordQuery(AccountStatementTransaction statementModel);

    Query transactionSortQuery();

    Query statementModelSort();

    void updateNeededFlags(Boolean updateAppAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport);

    void calculateAndUpdateAccountStatements(List<StatementModel> statementModelList);

    Update updateAccountTranBalanceDefination(StatementModel statementModel);

    public Update updateAccountBalanceDefination();

    MappingStrategy<? extends AccountStatementTransaction> getHeaderColumnNameModelMappingStrategy();


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    BigDecimal getAccountStatementBalance();


//    List<? extends ReportCollection> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList);


    void resetFields();


}
