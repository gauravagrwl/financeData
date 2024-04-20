package org.gauravagrwl.financeData.model.profileAccount.accountCollection;

import com.opencsv.bean.MappingStrategy;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.ReportCollection;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.AccountStatementDocument;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;

public interface UserAccountOperation {

    // List of abstract method:

    MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(String mappingProfile);

    public Update retrieveUpdateAccountDocumentQuery();

    Update getUpdateAccountStatementQuery(AccountStatementDocument accountStatementDocument);

    Query findDuplicateRecordQuery(AccountStatementDocument statementDocument);

    BigDecimal getAccountStatementBalance();


    void updateNeededFlags(Boolean updateAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport);

    List<? extends AccountStatementDocument> calculateAndUpdateAccountStatements(List<? extends AccountStatementDocument> statementDocumentList);

    List<? extends ReportCollection> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList);

    Query statementSortQuery();

    void resetFields();
}
