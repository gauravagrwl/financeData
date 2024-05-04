package org.gauravagrwl.financeData.model.profileAccount.accountCollection;

import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LoanAccountCollection extends AccountCollection {

    private BigDecimal loanAmount;
    private BigDecimal remaingAmount;
    private BigDecimal amountPaid;
    private String rateOfIntrest;
    private String purpose;
    private String remaingInstallment;

    @Override
    public MappingStrategy<? extends AccountStatementTransaction> getHeaderColumnNameModelMappingStrategy() {
        return null;
    }

    @Override
    public Update updateAccountBalanceDefination() {
        return null;
    }

    @Override
    public Update updateAccountTranBalanceDefination(StatementModel statementModel) {
        return null;
    }

    @Override
    public Query findDuplicateRecordQuery(AccountStatementTransaction statementModel) {
        return null;
    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return getLoanAmount();
    }

    @Override
    public void calculateAndUpdateAccountStatements(List<StatementModel> statementModelList) {
        
    }

//    @Override
//    public List<? extends ReportCollection> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList) {
//        return null;
//    }

    @Override
    public void updateNeededFlags(Boolean updateAppAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport) {
        this.setUpdateAccountAppStatementNeeded(updateAppAccountStatement);
        this.setUpdateAccountReportNeeded(updateAccountReport);
        this.setUpdateCashFlowReportNeeded(updateCashFlowReport);
    }

    @Override
    public Query statementSortQuery() {
        Sort sort = Sort.by(Sort.Direction.ASC, "transactionDate").and(Sort.by(Sort.Direction.ASC, "type"));
        Query query = new Query();
        query.with(sort);
        return query;
    }

    @Override
    public void resetFields() {

    }

}
