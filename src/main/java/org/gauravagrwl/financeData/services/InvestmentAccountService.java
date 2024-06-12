package org.gauravagrwl.financeData.services;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.DuplicateStatementRecords;
import org.gauravagrwl.financeData.helper.FinanceAppQuery;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.model.userAccounts.statements.BankAccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.statements.InvestmentAccountStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InvestmentAccountService {

    @Autowired
    MongoTemplate template;

    public void updateInvestmentAccountStatementDetails(UserAccount userAccount) {
    }

    public void insertBankAccountReportStatement(UserAccount userAccount) {
    }

    public void updateDuplicateInvestmentRecords(UserAccount userAccount) {
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
        return template.aggregate(duplicateRecordAggregation, InvestmentAccountStatement.class, DuplicateStatementRecords.class).getMappedResults();
    }
}
