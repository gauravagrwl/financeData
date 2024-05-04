package org.gauravagrwl.financeData.service;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.ReportCollection;
import org.gauravagrwl.financeData.model.profileAccount.reportCollection.investment.StockHoldingCollection;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;
import org.gauravagrwl.financeData.model.statementModel.StockInvestmentAccountStatementModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


    public void calculateAllAccountBalance() {

        profileService.getAllUserProfileDocument().forEach(userProfileDocument -> {
            for (AccountCollection userAccount : userProfileDocument.getUserAccounts()) {
                calculateUpdateAccountStatement(userAccount);
            }
        });
    }

    public void calculateUpdateAccountStatement(AccountCollection userAccount) {
        if (userAccount.getUpdateAccountAppStatementNeeded()) {
            log.info("Calculating account balance for account: " + userAccount.getAccountNumber());
            List<AccountStatementTransaction> accountStatementTransactionList = accountStatementDocumentService.getAccountTransactionStatementDocuments(userAccount);
            accountStatementTransactionList = accountStatementTransactionList.stream().filter(Predicate.not(AccountStatementTransaction::getReconciled)).collect(Collectors.toList());
            List<StatementModel> statementModelList = new ArrayList<>();
            for (AccountStatementTransaction transactionStatement : accountStatementTransactionList) {
                statementModelList.addAll(transactionStatement.updateAccountStatement(userAccount));

                //TODO: Reconcile after update is done.
                UpdateResult updateResult = template.updateFirst(FinanceDataHelper.findById(transactionStatement.getId()),
                        FinanceDataHelper.updateReconcileIndicatorDefination, AccountStatementTransaction.class, userAccount.getAccountTransactionCollectionName());
                log.info(updateResult.toString());
            }
            userAccount.calculateAndUpdateAccountStatements(statementModelList);
            if (userAccount.getUpdateAccountStatement()) {
                updateStatmentModelList(userAccount, statementModelList);
            }

            Update updateAccountBalanceDef = userAccount.updateAccountBalanceDefination();
            template.updateFirst(FinanceDataHelper.findById(userAccount.getId()), updateAccountBalanceDef, AccountCollection.class);

            userAccount.setBalanceCalculated(Boolean.TRUE);
            accountService.setisBalanceCalculatedeById(userAccount);
        } else {
            log.info("Balance is not calculated as flag is false for account: " + userAccount.getAccountNumber());
        }
    }

    private void updateStatmentModelList(AccountCollection userAccount, List<StatementModel> accountStatementModelList) {
        for (StatementModel statementModel : accountStatementModelList) {
            StatementModel insert = template.insert(statementModel, userAccount.getAccountStatementCollectionName());
            log.info("Statement saved with id: " + insert.getId());
        }
    }

    public void calculateUpdateAccountReport(AccountCollection userAccount) {
        if (userAccount.getUpdateAccountReportNeeded()) {
            log.info("Updating Account Reports: " + userAccount.getAccountNumber());

            List<StatementModel> statementModelList = accountStatementDocumentService.getAccountStatementDocuments(userAccount);
            updateStockHoldingReports(statementModelList, userAccount);
//            switch (userAccount.getProfileType()) {
//                case "Robinhood_STOCK" -> {
//                    updateStockHoldingReports(statementModelList, userAccount);
//                }
//                default -> {
//                }
//            }
        }
    }

    private void updateStockHoldingReports(List<StatementModel> statementModelList, AccountCollection userAccount) {
        for (StatementModel statementModel : statementModelList) {
            StockInvestmentAccountStatementModel statement = (StockInvestmentAccountStatementModel) statementModel;
            if (!StringUtils.isBlank(statement.getC_instrument()) && !statement.getReconciled()) {
                AccountStatementTransaction transaction = template.findOne(FinanceDataHelper.findById(statement.getAccountStatementId()), AccountStatementTransaction.class, userAccount.getAccountTransactionCollectionName());
                Query findByInstrumentName = new Query(Criteria.where("instrument").is(statement.findByKeyAssets()));
                List<ReportCollection> reportCollections = template.find(findByInstrumentName, ReportCollection.class, userAccount.getAccountReportCollectionName());
                if (reportCollections.size() == 1) {
                    StockHoldingCollection stockHolding = (StockHoldingCollection) reportCollections.get(0);
                    stockHolding.calculateHolding(statement);
                    stockHolding.getHoldingTransactionList().add(transaction);
                    template.save(stockHolding, userAccount.getAccountReportCollectionName());
                } else {
                    StockHoldingCollection newHolding = new StockHoldingCollection();
                    newHolding.setInstrument(statement.findByKeyAssets());
                    newHolding.setAccountDocumentId(userAccount.getId());
                    newHolding.calculateHolding(statement);
                    newHolding.getHoldingTransactionList().add(transaction);
                    template.insert(newHolding, userAccount.getAccountReportCollectionName());
                }
            }
            template.updateFirst(FinanceDataHelper.findById(statement.getId()), FinanceDataHelper.updateReconcileIndicatorDefination, StatementModel.class, userAccount.getAccountStatementCollectionName());
        }
    }
}
//        List<? extends AccountStatementDocument> accountStatementlist = (accountStatementDocumentService.getAccountStatementDocuments(userAccount)).stream().filter(accountStatementDocument -> !accountStatementDocument.getReconciled()).toList();
//        log.info("Total record need processing: " + accountStatementlist.size());
//        for (AccountStatementDocument accountStatement : accountStatementlist) {
//            switch (userAccount.getInstitutionCategory()) {
//                case BANKING -> {
//                    CashFlowHoldingDocument doc = new CashFlowHoldingDocument();
//                    doc.updateReport(accountStatement);
//                    doc.setAccountDocumentId(userAccount.getId());
//                    template.insert(doc, userAccount.getAccountReportCollectionName());
//                }
//                case INVESTMENT -> {
//                    if (!accountStatement.findKeyName().isBlank()) {
//                        Query findByInstrumentName = new Query(Criteria.where("instrument").is(accountStatement.findKeyName()));
//                        List<ReportCollection> reportCollections = template.find(findByInstrumentName, ReportCollection.class, userAccount.getAccountReportCollectionName());
//                        if (reportCollections.size() == 1) {
//                            HoldingCollection holdingCollection = (HoldingCollection) reportCollections.get(0);
//                            holdingCollection.calculateHolding_PartTwo(accountStatement);
//                            template.save(holdingCollection, userAccount.getAccountReportCollectionName());
//                        } else {
//                            HoldingCollection newHolding = new StockHoldingCollection();
//                            newHolding.setInstrument(accountStatement.findKeyName());
//                            newHolding.setAccountDocumentId(userAccount.getId());
//                            newHolding.calculateHolding_PartTwo(accountStatement);
//                            template.insert(newHolding, userAccount.getAccountReportCollectionName());
//                        }
//                    }
//
//
//                }
//need to re calculate the report again.

//            template.updateFirst(FinanceDataHelper.findById(accountStatement.getId()), updateReconcileIndicatorDefination, AccountStatementDocument.class, userAccount.getAccountStatementCollectionName());
//    }
//}

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
