package org.gauravagrwl.financeData.model.accountCollection;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.model.accountStatementModel.BankAccountStatementModel;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.banking.ChaseBankingAccountStatementTransaction;
import org.gauravagrwl.financeData.model.accountTransStatement.banking.SbiBankingAccountStatementTransaction;
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
            case "SBI_SAV" -> {
                MappingStrategy<SbiBankingAccountStatementTransaction> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<SbiBankingAccountStatementTransaction>()
                        .withForceCorrectRecordLength(true).build();
                headerColumnNameMappingStrategy.setProfile(getProfileType());
                headerColumnNameMappingStrategy.setType(SbiBankingAccountStatementTransaction.class);
                return headerColumnNameMappingStrategy;
            }
            default -> {
                log.error("No Maping strategy defined for :" + getProfileType());
//                throw new FinanceDataException("No Maping strategy defined for :" + getProfileType());
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
            case "SBI_SAV" -> {
                SbiBankingAccountStatementTransaction statement = (SbiBankingAccountStatementTransaction) statementModel;
                return new Query(
                        Criteria.where("s_Txn_Date").is(statement.getS_Txn_Date())
                                .and("s_Value_Date").is(statement.getS_Value_Date())
                                .and("s_Description").is(statement.getS_Description())
                                .and("s_Reference").is(statement.getS_Reference())
                                .and("s_Debit").is(statement.getS_Debit())
                                .and("s_Credit").is(statement.getS_Credit())
                                .and("s_Balance").is(statement.getS_Balance())
                );
            }
            default -> {
                throw new FinanceDataException("No duplicate query is defined for profile: " + getProfileType());
            }
        }
    }

    @Override
    public void updateNeededFlags(Boolean updateAppAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport) {
        this.setUpdateAccountStatementModelNeeded(updateAppAccountStatement);
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
    public Query transactionSortQuery() {
        Sort sort = null;
        Query query = new Query();
        switch (getProfileType()) {
            case "Chase_SAV", "Chase_CHK" -> {
                sort = Sort.by(Sort.Direction.ASC, "s_posting_Date").and(Sort.by(Sort.Direction.ASC, "s_type"));
            }
            case "SBI_SAV" -> {
                sort = Sort.by(Sort.Direction.ASC, "s_Txn_Date").and(Sort.by(Sort.Direction.DESC, "s_Credit"));
            }
            default -> {
                throw new FinanceDataException("No sort query is defined: " + getProfileType());
            }
        }
        return query.with(sort);
    }

    @Override
    public Query statementModelSort() {
        return new Query().with(Sort.by(Sort.Direction.ASC, "c_transactionDate").and(Sort.by(Sort.Direction.ASC, "s_type")));
    }

    @Override
    public void resetFields() {

        this.setUpdateAccountStatementModelNeeded(Boolean.FALSE);
        this.setUpdateAccountReportNeeded(Boolean.FALSE);
        this.setUpdateCashFlowReportNeeded(Boolean.FALSE);
        this.setHardStopDate(null);
        this.setIsActive(Boolean.TRUE);
        this.setBalanceCalculated(Boolean.FALSE);

        accountBalance = BigDecimal.ZERO;
    }

}
