package org.gauravagrwl.financeData.service;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FinanceDataSyncService {

    @Autowired
    MongoTemplate template;

    @Autowired
    ProfileService profileService;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountStatementDocumentService accountStatementDocumentService;

    public void calculateAllAccountBalance() {

        profileService.getAllUserProfileDocument().forEach(userProfileDocument -> {
            for (AccountDocument userAccount : userProfileDocument.getUserAccounts()) {
                calculateAccountBalance(userAccount);
            }
        });
    }

    public void calculateAccountBalance(AccountDocument userAccount) {
        if (!userAccount.getBalanceCalculatedFlag()) {
            log.info("Calculating account balance for account: " + userAccount.getAccountNumber());
            List<? extends AccountStatementDocument> updatedAccountStatement = userAccount.calculateAccountBalance(accountStatementDocumentService.getAccountStatementDocuments(userAccount));
            for (AccountStatementDocument accountStatementDocument : updatedAccountStatement) {
                Update updateDefination = Update.update("balance", accountStatementDocument.getCalculatedStatementBalance());
                template.updateFirst(FinanceDataHelper.findById(accountStatementDocument.getId()),
                        updateDefination, AccountStatementDocument.class, userAccount.getAccountStatementCollectionName());
            }
            accountService.setUpdateAccountBalanceById(userAccount, userAccount.getAccountStatementBalance());
            userAccount.setBalanceCalculatedFlag(Boolean.TRUE);
            accountService.setUpdateCalculateBalanceFlag(userAccount);
        } else {
            log.info("Balance is not calculated as flag is false for account: " + userAccount.getAccountNumber());
        }


    }


//    public void updateCashFlowDocuments(AccountDocument accountDocument,
//                                        List<BankAccountStatementDocument> bankAccountStatementList) {
//        bankAccountStatementList.forEach(statement -> {
//            if (!statement.getReconciled()) {
//                buildCashFlowTransaction(statement);
//            }
//        });
//
//    }

//    private void buildCashFlowTransaction(BankAccountStatementDocument accountStatement) {
//        CashFlowReportDocument cashFlowTransactionDocument = new CashFlowReportDocument();
//
//        cashFlowTransactionDocument.setTransactionDate(accountStatement.getTransactionDate());
//        cashFlowTransactionDocument.setYear(accountStatement.getTransactionDate().getYear());
//        cashFlowTransactionDocument.setDescription(accountStatement.getDescriptions());
//        cashFlowTransactionDocument.setCashIn(accountStatement.getCredit());
//        cashFlowTransactionDocument.setCashOut(accountStatement.getDebit());
//        cashFlowTransactionDocument.setAccountStatementId(accountStatement.getId());
//        if (cashFlowTransactionDocument.getCashIn().compareTo(BigDecimal.ZERO) > 0) {
//            cashFlowTransactionDocument.setTransactionType("CashIn");
//        } else {
//            cashFlowTransactionDocument.setTransactionType("CashOut");
//        }
//        cashFlowTransactionDocumentRepository.save(cashFlowTransactionDocument);
//        // accountStatement.setReconciled(Boolean.TRUE);
//        accountStatementDocumentRepository.findAndUpdateReconcileById(accountStatement.getId(),
//                Boolean.TRUE);

    // Query query = new Query(Criteria.where("id").is(accountStatement.getId()));
    // Update update = Update.update("reconciled", Boolean.TRUE);
    // template.updateFirst(query, update, BankAccountStatementDocument.class);
    // AccountStatementDocument accountStatementDocument =
    // accountStatementDocumentRepository
    // .findById(accountStatement.getId()).get();
    // accountStatementDocument.setReconciled(Boolean.TRUE);
    // accountStatementDocumentRepository.save(accountStatementDocument);

    // accountStatementDocumentRepository.save(accountStatement);
    // return cashFlowTransactionDocument;

//    }
}
