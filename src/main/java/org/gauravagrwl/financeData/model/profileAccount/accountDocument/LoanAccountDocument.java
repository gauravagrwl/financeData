package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.model.reports.AccountReportDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LoanAccountDocument extends AccountDocument {

    private BigDecimal loanAmount;
    private BigDecimal remaingAmount;
    private BigDecimal amountPaid;
    private String rateOfIntrest;
    private String purpose;
    private String remaingInstallment;

    @Override
    public MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(String mappingProfile) {
        return null;
    }

    @Override
    public Update retrieveUpdateAccountDocumentQuery() {
        return null;
    }

    @Override
    public Update getUpdateAccountStatementQuery(AccountStatementDocument accountStatementDocument) {
        return null;
    }

    @Override
    public Query findDuplicateRecordQuery(AccountStatementDocument statementDocument) {
        return null;
    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return getLoanAmount();
    }

    @Override
    public List<? extends AccountStatementDocument> calculateAndUpdateAccountStatements(List<? extends AccountStatementDocument> statementDocumentList) {
        return null;
    }

    @Override
    public List<? extends AccountReportDocument> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList) {
        return null;
    }

    @Override
    public void updateNeededStatementOrReports(Boolean updateAccountStatement, Boolean updateAccountReport) {
        this.setUpdateAccountStatement(updateAccountStatement);
        this.setUpdateAccountReport(updateAccountReport);
    }

    @Override
    public Query statementSortQuery() {
        Sort sort = Sort.by(Sort.Direction.ASC, "transactionDate").and(Sort.by(Sort.Direction.ASC, "type"));
        Query query = new Query();
        query.with(sort);
        return query;
    }
}
