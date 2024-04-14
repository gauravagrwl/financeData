package org.gauravagrwl.financeData.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.helper.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.BankAccountStatementDocument;
import org.gauravagrwl.financeData.model.repositories.AccountStatementDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class AccountDocumentService {

    private final MongoTemplate mongoTemplate;
    @Autowired
    private AccountStatementDocumentRepository accountStatementDocumentRepository;

    public AccountDocumentService(MongoTemplate mongoTemplate) {
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
                    Query query = FinanceDataHelper.findByTransactionDateAndAmountQuery((BankAccountStatementDocument) statement);
                    Update update = Update.update("duplicate", Boolean.TRUE);
                    UpdateResult updateMultiResult = mongoTemplate.updateMulti(query, update,
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
                mongoTemplate.updateFirst(FinanceDataHelper.findById(accountDocument.getId()), accountDocument.getBalanceCalculatedFlagQuery(Boolean.FALSE), AccountDocument.class);
                accountDocument.calculateAccountBalance();
                calculateAccountAndStatementBalance(accountDocument);
            }
            case INVESTMENT -> {
                accountStatementList.forEach(statement -> {
                    mongoTemplate.save(statement, documentCollectionName);
                });
                log.info("All statements are recorded");
            }
        }

        log.info("out saveAccountStatementDocuments");
    }

    /**
     * @param accountDocument
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public List<? extends AccountStatementDocument> getAccountStatementDocuments(
            AccountDocument accountDocument, Integer pageNumber, Integer pageSize) {
        log.info("in getAccountStatementDocuments with page number");
        Sort sort = Sort.by(Direction.ASC, "transactionDate").and(Sort.by(Direction.ASC, "type"));
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        List<AccountStatementDocument> accountStatementList = accountStatementDocumentRepository
                .findByAccountDocumentId(accountDocument.getId(), pageRequest);
        log.info("out getAccountStatementDocuments with page number");
        return accountStatementList;
    }

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
//        query.with(sort);
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

        Query query = FinanceDataHelper.findByTransactionDateAndAmountQuery((BankAccountStatementDocument) statementDocument);

        List<AccountStatementDocument> list = mongoTemplate.find(query, AccountStatementDocument.class,
                accountDocument.getAccountStatementCollectionName());

        if (list.size() == 1) {
            Update update = Update.update("duplicate", Boolean.FALSE);
            UpdateResult updateResult = mongoTemplate.updateFirst(FinanceDataHelper.findById(list.get(0).getId()), update,
                    AccountStatementDocument.class,
                    accountDocument.getAccountStatementCollectionName());
        }
        calculateAccountAndStatementBalance(accountDocument);
        log.info("out deleteAccountStatementDocument");
    }

    @SuppressWarnings("unchecked")
    private void calculateAccountAndStatementBalance(AccountDocument accountDocument) {
        log.info("in calculateAccountAndStatementBalance");
        if (InstitutionCategoryEnum.BANKING.compareTo(accountDocument.getInstitutionCategory()) == 0) {
            BigDecimal accountBalance = BigDecimal.ZERO;
            List<BankAccountStatementDocument> statementList = (List<BankAccountStatementDocument>) getAccountStatementDocuments(
                    accountDocument);
            for (BankAccountStatementDocument statement : statementList) {
                accountBalance = accountBalance.add(statement.getCredit())
                        .subtract(statement.getDebit());
                statement.setBalance(accountBalance);
                Update statementUpdateDefination = Update.update("balance", accountBalance);
                mongoTemplate.updateFirst(FinanceDataHelper.findById(statement.getId()), statementUpdateDefination,
                        AccountStatementDocument.class,
                        accountDocument.getAccountStatementCollectionName());
            }
            mongoTemplate.updateFirst(FinanceDataHelper.findById(accountDocument.getId()), accountDocument.getUpdateBalanceUpdateQuery(accountBalance), AccountDocument.class);
            mongoTemplate.updateFirst(FinanceDataHelper.findById(accountDocument.getId()), accountDocument.getBalanceCalculatedFlagQuery(Boolean.TRUE), AccountDocument.class);
        }
        log.info("out calculateAccountAndStatementBalance");

    }

    public void calculateAccountBalance() {
        log.info("in AccountService calculate balance");
    }

    // // TODO:
    // // Add filter for which all account balance can be calculated and be added
    // // Balance calculation: Bank Account expect credit (primary account)
    // // to cashflow statement for all cash in and cash out (primary account)
    // @SuppressWarnings("unchecked")
    // private void performAccountProcessing(AccountDocument accountDocument) {
    // if
    // (InstitutionCategoryEnum.BANKING.compareTo(accountDocument.getInstitutionCategory())
    // == 0) {
    // List<BankAccountStatementDocument> bankAccountStatementList =
    // (List<BankAccountStatementDocument>) getAccountStatementDocuments(
    // accountDocument);
    // if (AccountTypeEnum.CREDIT.compareTo(accountDocument.getAccountType()) != 0)
    // {
    // accountAsyncService.calculateAccountStatementBalance(accountDocument,
    // bankAccountStatementList);
    // }

    // accountAsyncService.updateCashFlowDocuments(accountDocument,
    // bankAccountStatementList);
    // }
    // }
}
