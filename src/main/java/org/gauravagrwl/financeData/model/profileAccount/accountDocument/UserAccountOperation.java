package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.opencsv.bean.MappingStrategy;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.model.reports.AccountReportDocument;
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


    void updateNeededStatementOrReports(Boolean updateAccountStatement, Boolean updateAccountReport);

    List<? extends AccountStatementDocument> calculateAndUpdateAccountStatements(List<? extends AccountStatementDocument> statementDocumentList);

    List<? extends AccountReportDocument> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList);
}
