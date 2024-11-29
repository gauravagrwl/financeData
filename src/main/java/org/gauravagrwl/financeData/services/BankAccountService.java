package org.gauravagrwl.financeData.services;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.DuplicateStatementRecords;
import org.gauravagrwl.financeData.helper.FinanceAppQuery;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.model.userAccounts.reports.CashFlowReportStatement;
import org.gauravagrwl.financeData.model.userAccounts.reports.ReportStatement;
import org.gauravagrwl.financeData.model.userAccounts.statements.BankAccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class BankAccountService {
    @Autowired
    MongoTemplate template;

    public void deleteBankAccountStatement(UserAccount userAccount, String accountStatementId) {
        DuplicateStatementRecords duplicateStatementRecord = getDuplicateRecordsList(userAccount).stream().filter(r -> r.getIds().contains(accountStatementId)).findFirst().get();
        ;
        BankAccountStatement accountStatement = template.findOne(FinanceAppQuery.findByIdQuery(accountStatementId), BankAccountStatement.class);
        String accountTransactionId = accountStatement.getAccountTransactionIds().stream().findFirst().orElse("");
        String reportId = accountStatement.getReportStatement().getId();
        template.remove(FinanceAppQuery.findByIdQuery(reportId), CashFlowReportStatement.class);
        template.remove(FinanceAppQuery.findByIdQuery(accountStatementId), BankAccountStatement.class);
        template.remove(FinanceAppQuery.findByIdQuery(accountTransactionId), AccountTransaction.class, userAccount.getAccountTransactionCollectionName());
        updateBankAccountStatementBalance(userAccount);

        if (duplicateStatementRecord.getCount() == 2) {
            duplicateStatementRecord.getIds().remove(accountStatementId);
            String id = duplicateStatementRecord.getIds().get(0);
            UpdateResult updateResult = template.updateFirst(
                    FinanceAppQuery.findByIdQuery(id),
                    FinanceAppQuery.updateBooleanValueIndicator("duplicate", Boolean.FALSE),
                    BankAccountStatement.class);
        }
    }


    public void updateBankAccountStatementBalance(UserAccount userAccount) {
        List<BankAccountStatement> accountStatementList =
                template.find(FinanceAppQuery.findAndSortAllBankStatementQuery(userAccount.getId()), BankAccountStatement.class);
        BigDecimal transactionBalance = BigDecimal.ZERO.setScale(2);
        for (BankAccountStatement statement : accountStatementList) {
            if (statement.getType().equalsIgnoreCase("Cr.")) {
                transactionBalance = transactionBalance.add(statement.getAmount());
            } else {
                transactionBalance = transactionBalance.subtract(statement.getAmount());
            }
            statement.setTransactionBalance(transactionBalance);
            UpdateDefinition updateDefinition = Update.update("transactionBalance", transactionBalance);
            UpdateResult updateResult = template.updateFirst(FinanceAppQuery.findByIdQuery(statement.getId()), updateDefinition, BankAccountStatement.class);
            log.info("Transaction Balance Updated {}", updateResult.wasAcknowledged());
        }
        UpdateDefinition updateDefinition = Update.update("accountBalance", transactionBalance);
        UpdateResult updateResult = template.updateFirst(FinanceAppQuery.findByIdQuery(userAccount.getId()), updateDefinition, UserAccount.class);
        log.info("Account Balance Updated {}", updateResult.wasAcknowledged());
    }

    public void insertBankAccountReportStatement(UserAccount userAccount) {
        List<BankAccountStatement> accountStatementList =
                template.find(FinanceAppQuery.findAndSortAllBankStatementQuery(userAccount.getId()), BankAccountStatement.class);
        List<BankAccountStatement> accountStatementForProcessingList = accountStatementList.stream()
                .filter(
                        accountStatement ->
                                (null == accountStatement.getReportStatement() || null == accountStatement.getReportStatement().getId())
                ).toList();
        for (BankAccountStatement accountStatement : accountStatementForProcessingList) {
            ReportStatement reportStatement = transformToReports(accountStatement);
            template.insert(reportStatement);
        }
    }

    private CashFlowReportStatement transformToReports(BankAccountStatement accountStatement) {
        CashFlowReportStatement report = new CashFlowReportStatement();
        report.setAccountStatementId(accountStatement.getId());
        report.setAccountId(accountStatement.getAccountId());

        report.setTransactionDate(accountStatement.getTransactionDate());
        report.setYear(accountStatement.getTransactionDate().getYear());
        report.setDescription(accountStatement.getDescription());
        report.setAmount(accountStatement.getAmount());
        report.setType(accountStatement.getType());
        return report;
    }


    public void updateDuplicateBankRecords(UserAccount userAccount) {
        List<DuplicateStatementRecords> duplicateRecordsList = getDuplicateRecordsList(userAccount);
        for (DuplicateStatementRecords duplicateStatementRecords : duplicateRecordsList) {
            duplicateStatementRecords.getIds().forEach(id -> {
                UpdateResult updateResult = template.updateFirst(FinanceAppQuery.findByIdQuery(id), FinanceAppQuery.updateBooleanValueIndicator("duplicate", Boolean.TRUE), BankAccountStatement.class);
                if (updateResult.wasAcknowledged()) {
                    log.info("Success: Duplicate Indicator updated for id: {} ", id);
                } else {
                    log.info("Failure: Duplicate Indicator update for id: {} ", id);
                }
            });
        }
    }

    private List<DuplicateStatementRecords> getDuplicateRecordsList(UserAccount userAccount) {
        Aggregation duplicateRecordAggregation = FinanceAppQuery.findDuplicateTransactionAggregationQuery(userAccount);
        log.info(duplicateRecordAggregation.toString());
        return template.aggregate(duplicateRecordAggregation, BankAccountStatement.class, DuplicateStatementRecords.class).getMappedResults();
    }
}
