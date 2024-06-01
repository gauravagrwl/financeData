package org.gauravagrwl.financeData.model.accountCollection;

import com.opencsv.bean.CsvToBean;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssetsAccountCollection extends AccountCollection {

    // Total amount for the purchase.
    private BigDecimal amountInvestment;

    // Asset name
    private String propertyName;

    // Asset acquired Year
    private String acquiredYear;

    private boolean isRentable;

    private String tenantName;

    private String tenantNumber;


    @Override
    public void calculateAndUpdateAccountStatements(List<StatementModel> statementModelList) {

    }

//    @Override
//    public List<? extends ReportCollection> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList) {
//        return null;
//    }

    @Override
    public Update updateAccountBalanceDefination() {
        return null;
    }

    @Override
    public CsvToBean<AccountStatementTransaction> getCsvStatementMapperToBean(InputStreamReader reader) {
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
        return getAmountInvestment();
    }

    @Override
    public void updateNeededFlags(Boolean updateAppAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport) {
        this.setUpdateAccountStatementModelNeeded(updateAppAccountStatement);
        this.setUpdateAccountReportNeeded(updateAccountReport);
        this.setUpdateCashFlowReportNeeded(updateCashFlowReport);
    }

    @Override
    public Query transactionSortQuery() {
        Sort sort = Sort.by(Sort.Direction.ASC, "transactionDate").and(Sort.by(Sort.Direction.ASC, "type"));
        Query query = new Query();
        query.with(sort);
        return query;
    }

    @Override
    public Query statementModelSort() {
        return null;
    }

    @Override
    public void resetFields() {

    }

}
