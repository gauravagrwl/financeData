package org.gauravagrwl.financeData.service;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.HoldingCollection;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.ReportCollection;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.StockHoldingCollection;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.AccountStatementDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FinanceDataCommonService {

    @Autowired
    MongoTemplate template;

    @Autowired
    ProfileService profileService;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountStatementDocumentService accountStatementDocumentService;

    Update updateReconcileIndicatorDefination = Update.update("reconciled", Boolean.TRUE);


    public void calculateAllAccountBalance() {

        profileService.getAllUserProfileDocument().forEach(userProfileDocument -> {
            for (AccountCollection userAccount : userProfileDocument.getUserAccounts()) {
                calculateUpdateAccountStatement(userAccount);
            }
        });
    }

    public void calculateUpdateAccountStatement(AccountCollection userAccount) {
        if (userAccount.getUpdateAccountStatementNeeded()) {
            log.info("Calculating account balance for account: " + userAccount.getAccountNumber());
            List<? extends AccountStatementDocument> updatedAccountStatementList = userAccount.calculateAndUpdateAccountStatements(accountStatementDocumentService.getAccountStatementDocuments(userAccount));
            for (AccountStatementDocument accountStatementDocument : updatedAccountStatementList) {
                Update updateDefination = userAccount.getUpdateAccountStatementQuery(accountStatementDocument);
                template.updateFirst(FinanceDataHelper.findById(accountStatementDocument.getId()),
                        updateDefination, AccountStatementDocument.class, userAccount.getAccountStatementCollectionName());
            }
            Update updateAccountDocument = userAccount.retrieveUpdateAccountDocumentQuery();
            template.updateFirst(FinanceDataHelper.findById(userAccount.getId()), updateAccountDocument, AccountCollection.class);

//            accountService.setUpdateAccountBalanceById(userAccount, userAccount.getAccountStatementBalance());
            userAccount.setBalanceCalculated(Boolean.TRUE);
            accountService.setisBalanceCalculatedeById(userAccount);
        } else {
            log.info("Balance is not calculated as flag is false for account: " + userAccount.getAccountNumber());
        }
        // Not correct need to re-work causing duplicate.
        calculateUpdateAccountReport(userAccount);
    }

    public void calculateUpdateAccountReport(AccountCollection userAccount) {
        List<? extends AccountStatementDocument> accountStatementlist = (accountStatementDocumentService.getAccountStatementDocuments(userAccount)).stream().filter(accountStatementDocument -> !accountStatementDocument.getReconciled()).toList();
        log.info("Total record need processing: " + accountStatementlist.size());
        switch (userAccount.getInstitutionCategory()) {
            case BANKING -> {

            }
            case INVESTMENT -> {
                for (AccountStatementDocument accountStatement : accountStatementlist) {
                    if (!accountStatement.findKeyName().isBlank()) {
                        Query findByInstrumentName = new Query(Criteria.where("instrument").is(accountStatement.findKeyName()));
                        List<ReportCollection> reportCollections = template.find(findByInstrumentName, ReportCollection.class, userAccount.getAccountReportCollectionName());
                        if (reportCollections.size() == 1) {
                            HoldingCollection holdingCollection = (HoldingCollection) reportCollections.get(0);
                            holdingCollection.calculateHolding_PartTwo(accountStatement);
                            template.save(holdingCollection, userAccount.getAccountReportCollectionName());
                        } else {
                            HoldingCollection newHolding = new StockHoldingCollection();
                            newHolding.setInstrument(accountStatement.findKeyName());
                            newHolding.setAccountDocumentId(userAccount.getId());
                            newHolding.calculateHolding_PartTwo(accountStatement);
                            template.insert(newHolding, userAccount.getAccountReportCollectionName());
                        }
                    }

                    template.updateFirst(FinanceDataHelper.findById(accountStatement.getId()), updateReconcileIndicatorDefination, AccountStatementDocument.class, userAccount.getAccountStatementCollectionName());
                }
                //need to re calculate the report again.
            }
        }
    }
}

//                public void calculateUpdateAccountReport(AccountCollection userAccount) {
//                    List<? extends AccountStatementDocument> accountStatementlist = accountStatementDocumentService.getAccountStatementDocuments(userAccount);
//
//                    if (userAccount.getUpdateAccountReportNeeded()) {
//                        List<? extends HoldingCollection> updateReportList = userAccount.calculateAndUpdateAccountReports(accountStatementlist);
//                        for (HoldingCollection ard : updateReportList) {
//                            template.save(ard, userAccount.getAccountReportCollectionName());
//                        }
//                    }
//
//                }


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
