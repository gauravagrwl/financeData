package org.gauravagrwl.financeData.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.gauravagrwl.financeData.helper.FinanceDataHelper.updateDuplicateIndicatorDefination;

@Service
@Slf4j
public class AccountStatementModelService {

    private final MongoTemplate template;

    @Autowired
    AccountService accountService;


    public AccountStatementModelService(MongoTemplate template) {
        this.template = template;
    }

    public List<StatementModel> getAccountStatementDocuments(
            AccountCollection accountCollection) {
        log.info("in getAccountStatementDocuments with sort");
        Query query = accountCollection.statementModelSort();
        // PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        List<StatementModel> accountStatementList = template.find(query,
                StatementModel.class,
                accountCollection.getAccountStatementCollectionName());
        log.info("out getAccountStatementDocuments with sort");
        return accountStatementList;
    }

    public void deleteAccountStatementDocument(AccountCollection accountCollection, String statementId) {
        log.info("in deleteAccountStatementDocument");

        StatementModel statementModelDocument = template.findOne(FinanceDataHelper.findById(statementId),
                StatementModel.class, accountCollection.getAccountStatementCollectionName());

        AccountStatementTransaction statementTransaction = template.findOne(FinanceDataHelper.findById(statementModelDocument.getAccountTransactionId()),
                AccountStatementTransaction.class, accountCollection.getAccountTransactionCollectionName());

        DeleteResult deleteTransactionResult = template.remove(FinanceDataHelper.findById(statementModelDocument.getAccountTransactionId()), AccountStatementTransaction.class,
                accountCollection.getAccountTransactionCollectionName());

        DeleteResult deleteStatementResult = template.remove(FinanceDataHelper.findById(statementId), StatementModel.class,
                accountCollection.getAccountStatementCollectionName());

        Query query = null; //accountCollection.findDuplicateRecordQuery(statementDocument, accountCollection.getProfileType());
        List<AccountStatementTransaction> list = template.find(query, AccountStatementTransaction.class,
                accountCollection.getAccountStatementCollectionName());

        if (list.size() == 1) {
            Update update = Update.update("duplicate", Boolean.FALSE);
            UpdateResult updateResult = template.updateFirst(FinanceDataHelper.findById(list.get(0).getId()), update,
                    AccountStatementTransaction.class,
                    accountCollection.getAccountStatementCollectionName());
        }
        accountCollection.updateNeededFlags(true, true, true);
        accountService.setUpdateCalculateBalanceFlag(accountCollection);
        log.info("out deleteAccountStatementDocument");
    }


    public List<AccountStatementTransaction> getAccountTransactionStatementDocuments(AccountCollection accountCollection) {
        log.info("in getAccountStatementDocuments with sort");
        Query query = accountCollection.transactionSortQuery();
        // PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        List<AccountStatementTransaction> accountStatementList = template.find(query,
                AccountStatementTransaction.class,
                accountCollection.getAccountTransactionCollectionName());
        log.info("out getAccountStatementDocuments with sort");
        return accountStatementList;
    }

    public void saveAccountTransactionList(List<AccountStatementTransaction> statementModelList, AccountCollection accountCollection) {
        log.info("Saving account statement for account: " + FinanceDataHelper.getAccountDisplayNumber(accountCollection.getAccountNumber()));
        String accountStatementCollectionName = accountCollection.getAccountTransactionCollectionName();
        for (AccountStatementTransaction accountStatementTransaction : statementModelList) {
            accountStatementTransaction.setAccountDocumentId(accountCollection.getId());
            Query query = accountCollection.findDuplicateRecordQuery(accountStatementTransaction);
            UpdateResult updateMultiResult = template.updateMulti(query, updateDuplicateIndicatorDefination,
                    AccountStatementTransaction.class, accountStatementCollectionName);
            if (updateMultiResult.getMatchedCount() > 0) {
                log.warn("Total Duplicate Records found: " + updateMultiResult.getMatchedCount()
                        + "and total updated records are: "
                        + updateMultiResult.getModifiedCount());
                accountStatementTransaction.setDuplicate(Boolean.TRUE);
            }
            template.insert(accountStatementTransaction, accountStatementCollectionName);
        }
        log.info("All statements are recorded for account: " + FinanceDataHelper.getAccountDisplayNumber(accountCollection.getAccountNumber()));
        accountCollection.updateNeededFlags(true, true, true);
        accountService.setUpdateCalculateBalanceFlag(accountCollection);
        log.info("out saveAccountStatementDocuments");
    }
}
