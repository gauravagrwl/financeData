package org.gauravagrwl.financeData.model.profileAccount.accountCollection;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.CashFlowHoldingDocument;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.BankAccountStatementDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    public MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(
            String mappingProfile) {
        MappingStrategy<BankAccountStatementDocument> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<BankAccountStatementDocument>()
                .withForceCorrectRecordLength(true).build();
        headerColumnNameMappingStrategy.setProfile(mappingProfile);
        headerColumnNameMappingStrategy.setType(BankAccountStatementDocument.class);
        return headerColumnNameMappingStrategy;
    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return accountBalance;
    }

    @Override
    public Update retrieveUpdateAccountDocumentQuery() {
        return Update.update("accountBalance", accountBalance);
    }

    @Override
    public Update getUpdateAccountStatementQuery(AccountStatementDocument accountStatementDocument) {
        BankAccountStatementDocument statement = (BankAccountStatementDocument) accountStatementDocument;
        return Update.update("balance", statement.getBalance());
    }

    @Override
    public Query findDuplicateRecordQuery(AccountStatementDocument statementDocument) {
        BankAccountStatementDocument statement = (BankAccountStatementDocument) statementDocument;
        return new Query(
                Criteria.where("transactionDate").is(statement.getTransactionDate()).and("descriptions")
                        .is(statement.getDescriptions()).and("type").is(statement.getType())
                        .and("debit")
                        .is(statement.getDebit())
                        .and("credit")
                        .is(statement.getCredit()));
    }

    @Override
    public void updateNeededFlags(Boolean updateAccountStatement, Boolean updateAccountReport, Boolean updateCashFlowReport) {
        this.setUpdateAccountStatementNeeded(updateAccountStatement);
        this.setUpdateAccountReportNeeded(updateAccountReport);
        this.setUpdateCashFlowReportNeeded(updateCashFlowReport);
    }

    @Override
    public List<? extends AccountStatementDocument> calculateAndUpdateAccountStatements(List<? extends AccountStatementDocument> statementDocumentList) {
        List<BankAccountStatementDocument> statementList = (List<BankAccountStatementDocument>) statementDocumentList;
        accountBalance = BigDecimal.ZERO;
        for (BankAccountStatementDocument statement : statementList) {
            accountBalance = accountBalance.add(statement.getCredit())
                    .subtract(statement.getDebit());
            statement.setBalance(accountBalance);
        }
        return statementList;
    }

    @Override
    public List<CashFlowHoldingDocument> calculateAndUpdateAccountReports(List<? extends AccountStatementDocument> accountStatementList) {
        List<CashFlowHoldingDocument> cashFlowReportDocumentList = new ArrayList<>();
        return cashFlowReportDocumentList;
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

        this.setUpdateAccountStatementNeeded(Boolean.FALSE);
        this.setUpdateAccountReportNeeded(Boolean.FALSE);
        this.setUpdateCashFlowReportNeeded(Boolean.FALSE);
        this.setHardStopDate(null);
        this.setIsActive(Boolean.TRUE);
        this.setBalanceCalculated(Boolean.FALSE);

        accountBalance = BigDecimal.ZERO;
    }

}
