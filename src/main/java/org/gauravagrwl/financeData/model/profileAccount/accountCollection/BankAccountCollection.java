package org.gauravagrwl.financeData.model.profileAccount.accountCollection;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.banking.ChaseBankingAccountStatementTransaction;
import org.gauravagrwl.financeData.model.statementModel.BankAccountStatementModel;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class supports below Institution Sub Category:
 * <p>
 * CHECKING(InstitutionCategoryEnum.BANKING, "CHK", "101"),
 * SAVING(InstitutionCategoryEnum.BANKING, "SAV", "102"),
 * DEPOSIT(InstitutionCategoryEnum.BANKING, "DEP", "103"),
 * PPF(InstitutionCategoryEnum.BANKING, "PPF", "104"),
 * CREDIT(InstitutionCategoryEnum.BANKING, "CRE", "105"),
 */


@Slf4j
public class BankAccountCollection extends AccountCollection {

    // Account Calculated Balance
    @Getter
    private BigDecimal accountBalance = BigDecimal.ZERO;

    // Account holding type
    @Getter
    @Setter
    private String holdingType;

    // Account Code: Routing code or IIFC code.
    @Getter
    @Setter
    private String accountCode;

    // Code Type: Routing or IIFC
    @Getter
    @Setter
    private String accountCodeType;

    @Override
    public MappingStrategy<? extends AccountStatementTransaction> getHeaderColumnNameModelMappingStrategy() {
        switch (getProfileType()) {
            case "Chase_SAV", "Chase_CHK" -> {
                MappingStrategy<ChaseBankingAccountStatementTransaction> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<ChaseBankingAccountStatementTransaction>()
                        .withForceCorrectRecordLength(true).build();
                headerColumnNameMappingStrategy.setProfile(getProfileType());
                headerColumnNameMappingStrategy.setType(ChaseBankingAccountStatementTransaction.class);
                return headerColumnNameMappingStrategy;
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return accountBalance;
    }

    @Override
    public Update updateAccountBalanceDefination() {
        return Update.update("accountBalance", accountBalance);
    }

    @Override
    public Update updateAccountTranBalanceDefination(StatementModel accountStatementModel) {
        BankAccountStatementModel statementModel = (BankAccountStatementModel) accountStatementModel;
        return Update.update("c_transactionBalance", statementModel.getC_transactionBalance());
    }

    @Override
    public Query findDuplicateRecordQuery(AccountStatementTransaction statementModel) {
        switch (getProfileType()) {
            case "Chase_SAV", "Chase_CHK" -> {
                ChaseBankingAccountStatementTransaction statement = (ChaseBankingAccountStatementTransaction) statementModel;
                return new Query(
                        Criteria.where("s_posting_Date").is(statement.getS_posting_Date()).and("s_details")
                                .is(statement.getS_details()).and("s_amount").is(statement.getS_amount())
                                .and("s_balance").is(statement.getS_balance()));
            }
            default -> {
                return null;
            }
        }

    }

    @Override
    public void updateNeededFlags(Boolean updateAppAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport) {
        this.setUpdateAccountAppStatementNeeded(updateAppAccountStatement);
        this.setUpdateAccountReportNeeded(updateAccountReport);
        this.setUpdateCashFlowReportNeeded(updateCashFlowReport);
    }

    @Override
    public void calculateAndUpdateAccountStatements(List<StatementModel> statementModelList) {
        for (StatementModel statementModel : statementModelList) {
            BankAccountStatementModel statement = (BankAccountStatementModel) statementModel;
            accountBalance = accountBalance.add(statement.getC_credit())
                    .subtract(statement.getC_debit());
            statement.setC_transactionBalance(accountBalance);
        }
        setUpdateAccountStatement(Boolean.TRUE);
    }


    @Override
    public Query statementSortQuery() {
        Sort sort = null;
        Query query = new Query();
        switch (getProfileType()) {
            case "Chase_SAV", "Chase_CHK" -> {
                sort = Sort.by(Sort.Direction.ASC, "s_posting_Date").and(Sort.by(Sort.Direction.ASC, "c_type"));
            }
        }

        return (sort != null) ? query.with(sort) : query;

    }

    @Override
    public void resetFields() {

        this.setUpdateAccountAppStatementNeeded(Boolean.FALSE);
        this.setUpdateAccountReportNeeded(Boolean.FALSE);
        this.setUpdateCashFlowReportNeeded(Boolean.FALSE);
        this.setHardStopDate(null);
        this.setIsActive(Boolean.TRUE);
        this.setBalanceCalculated(Boolean.FALSE);

        accountBalance = BigDecimal.ZERO;
    }

}
