package org.gauravagrwl.financeData.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountStatementDocumentService {

    private final MongoTemplate template;

    @Autowired
    AccountService accountService;
    Update updateDuplicateIndicatorDefination = Update.update("duplicate", Boolean.TRUE);


    public AccountStatementDocumentService(MongoTemplate template) {
        this.template = template;
    }


    /**
     * @param accountDocument
     * @param pageNumber
     * @param pageSize
     * @return
     */
//    public List<? extends AccountStatementDocument> getAccountStatementDocuments(
//            AccountDocument accountDocument, Integer pageNumber, Integer pageSize) {
//        log.info("in getAccountStatementDocuments with page number");
//        Sort sort = Sort.by(Direction.ASC, "transactionDate").and(Sort.by(Direction.ASC, "type"));
//        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
//        List<AccountStatementDocument> accountStatementList = accountStatementDocumentRepository
//                .findByAccountDocumentId(accountDocument.getId(), pageRequest);
//        log.info("out getAccountStatementDocuments with page number");
//        return accountStatementList;
//    }

    /**
     * With sorted only.
     *
     * @param accountCollection
     * @return
     */
    public List<AccountStatementTransaction> getAccountTransactionStatementDocuments(
            AccountCollection accountCollection) {
        log.info("in getAccountStatementDocuments with sort");
        Query query = accountCollection.statementSortQuery();
        // PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        List<AccountStatementTransaction> accountStatementList = template.find(query,
                AccountStatementTransaction.class,
                accountCollection.getAccountTransactionCollectionName());
        log.info("out getAccountStatementDocuments with sort");
        return accountStatementList;
    }

    public List<StatementModel> getAccountStatementDocuments(
            AccountCollection accountCollection) {
        log.info("in getAccountStatementDocuments with sort");
        Query query = accountCollection.statementSortQuery();
        // PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        List<StatementModel> accountStatementList = template.find(query,
                StatementModel.class,
                accountCollection.getAccountStatementCollectionName());
        log.info("out getAccountStatementDocuments with sort");
        return accountStatementList;
    }


    public void deleteAccountStatementDocument(AccountCollection accountCollection, String statementId) {
        log.info("in deleteAccountStatementDocument");

        AccountStatementTransaction statementDocument = template.findOne(FinanceDataHelper.findById(statementId),
                AccountStatementTransaction.class, accountCollection.getAccountStatementCollectionName());

        DeleteResult deleteResult = template.remove(FinanceDataHelper.findById(statementId), AccountStatementTransaction.class,
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

    //Insert Statment as it is..
    public void saveAccountStatementModelList(List<AccountStatementTransaction> statementModelList, AccountCollection accountCollection) {
        log.info("Saving account statement for account: " + FinanceDataHelper.getAccountDisplayNumber(accountCollection.getAccountNumber()));
        String accountStatementCollectionName = accountCollection.getAccountTransactionCollectionName();
        for (AccountStatementTransaction accountStatementTransaction : statementModelList) {
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
