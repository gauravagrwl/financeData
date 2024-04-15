package org.gauravagrwl.financeData.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountStatementDocumentService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    AccountService accountService;
    Update updateDuplicateIndicatorDefination = Update.update("duplicate", Boolean.TRUE);

    public AccountStatementDocumentService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public void saveAccountStatementDocuments(List<? extends AccountStatementDocument> accountStatementList,
                                              AccountDocument accountDocument) {
        String documentCollectionName = accountDocument.getAccountStatementCollectionName();
        log.info("in saveAccountStatementDocuments");
        switch (accountDocument.getInstitutionCategory()) {
            case BANKING -> {
                accountDocument.getAccountStatementBalance();
                accountStatementList.forEach(statement -> {
                    Query query = accountDocument.findDuplicateRecordQuery(statement);
                    UpdateResult updateMultiResult = mongoTemplate.updateMulti(query, updateDuplicateIndicatorDefination,
                            AccountStatementDocument.class, documentCollectionName);
                    if (updateMultiResult.getMatchedCount() > 0) {
                        log.warn("Total Duplicate Records found: " + updateMultiResult.getMatchedCount()
                                + "and total updated records are: "
                                + updateMultiResult.getModifiedCount());
                        statement.setDuplicate(Boolean.TRUE);
                    }
                    mongoTemplate.save(statement, documentCollectionName);
                });
                log.info("All statements are recorded");
            }
            case LOAN, INVESTMENT, ASSETS -> {
                accountStatementList.forEach(statement -> {
                    mongoTemplate.save(statement, documentCollectionName);
                });

                log.info("All statements are recorded");
            }
        }
        accountDocument.balanceCalculationNeeded();
        accountService.setUpdateCalculateBalanceFlag(accountDocument);
        log.info("out saveAccountStatementDocuments");
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
     * @param accountDocument
     * @return
     */
    public List<? extends AccountStatementDocument> getAccountStatementDocuments(
            AccountDocument accountDocument) {
        log.info("in getAccountStatementDocuments with sort");
        Sort sort = Sort.by(Direction.ASC, "transactionDate").and(Sort.by(Direction.ASC, "type"));
        Query query = new Query();
        query.with(sort);
        // PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        List<AccountStatementDocument> accountStatementList = mongoTemplate.find(query,
                AccountStatementDocument.class,
                accountDocument.getAccountStatementCollectionName());
        log.info("in getAccountStatementDocuments with sort");
        return accountStatementList;
    }

    public void deleteAccountStatementDocument(AccountDocument accountDocument, String statementId) {
        log.info("in deleteAccountStatementDocument");

        AccountStatementDocument statementDocument = mongoTemplate.findOne(FinanceDataHelper.findById(statementId),
                AccountStatementDocument.class, accountDocument.getAccountStatementCollectionName());

        DeleteResult deleteResult = mongoTemplate.remove(FinanceDataHelper.findById(statementId), AccountStatementDocument.class,
                accountDocument.getAccountStatementCollectionName());

        Query query = accountDocument.findDuplicateRecordQuery(statementDocument);
        List<AccountStatementDocument> list = mongoTemplate.find(query, AccountStatementDocument.class,
                accountDocument.getAccountStatementCollectionName());

        if (list.size() == 1) {
            Update update = Update.update("duplicate", Boolean.FALSE);
            UpdateResult updateResult = mongoTemplate.updateFirst(FinanceDataHelper.findById(list.get(0).getId()), update,
                    AccountStatementDocument.class,
                    accountDocument.getAccountStatementCollectionName());
        }
        accountDocument.balanceCalculationNeeded();
        accountService.setUpdateCalculateBalanceFlag(accountDocument);
        log.info("out deleteAccountStatementDocument");
    }

}
