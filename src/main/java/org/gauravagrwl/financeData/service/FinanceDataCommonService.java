package org.gauravagrwl.financeData.service;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.helper.enums.Category_I;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountReportsModel.ReportCollection;
import org.gauravagrwl.financeData.model.accountReportsModel.banking.CashFlowReportCollection;
import org.gauravagrwl.financeData.model.accountReportsModel.investment.StockHoldingCollection;
import org.gauravagrwl.financeData.model.accountStatementModel.BankAccountStatementModel;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountStatementModel.StockInvestmentAccountStatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
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
    AccountStatementModelService accountStatementModelService;

    @Autowired
    AccountStatementModelService statementService;


    public void calculateAllAccountBalance() {

        profileService.getAllUserProfileDocument().forEach(userProfileDocument -> {
            for (AccountCollection userAccount : userProfileDocument.getUserAccounts()) {
                calculateUpdateAccountStatement(userAccount);
            }
        });
    }

    public void calculateUpdateAccountStatement(AccountCollection userAccount) {
        if (userAccount.getUpdateAccountStatementModelNeeded()) {
            log.info("Calculating account balance for account: " + userAccount.getAccountNumber());
            List<AccountStatementTransaction> accountStatementTransactionList = statementService.getAccountTransactionStatementDocuments(userAccount);
            accountStatementTransactionList = accountStatementTransactionList.stream().filter(Predicate.not(AccountStatementTransaction::getReconciled)).collect(Collectors.toList());
            List<StatementModel> statementModelList = new ArrayList<>();
            for (AccountStatementTransaction transactionStatement : accountStatementTransactionList) {
                statementModelList.addAll(transactionStatement.updateAccountStatement(userAccount));
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
            String transactionStatementId = insert.getAccountStatementId();
            log.info("Statement saved with id: " + insert.getId());
            UpdateResult updateResult = template.updateFirst(FinanceDataHelper.findById(transactionStatementId),
                    FinanceDataHelper.updateReconcileIndicatorDefination, AccountStatementTransaction.class, userAccount.getAccountTransactionCollectionName());
            log.info(updateResult.toString());


        }
    }

    public void calculateUpdateAccountReport(AccountCollection userAccount) {
        if (userAccount.getUpdateAccountReportNeeded()) {
            log.info("Updating Account Reports: " + userAccount.getAccountNumber());
            List<StatementModel> statementModelList = accountStatementModelService.getAccountStatementDocuments(userAccount);
            switch (userAccount.getInstitutionCategory()) {
                case INVESTMENT -> {
                    updateStockHoldingReports(statementModelList, userAccount);
                }
                case BANKING -> {
                    updateCashFlowReports(statementModelList, userAccount);
                }
                default -> {
                    log.warn("No report operation defined for: " + userAccount.getInstitutionCategory());
                }
            }
        }
    }

    private void updateCashFlowReports(List<StatementModel> statementModelList, AccountCollection userAccount) {
        for (StatementModel statementModel : statementModelList) {
            BankAccountStatementModel statement = (BankAccountStatementModel) statementModel;
            List<CashFlowReportCollection> cashFlowReportCollectionList = new ArrayList<>();
            if (!statement.getReconciled()) {
                CashFlowReportCollection cashFlowReport = getCashFlowReportCollection(userAccount, statement);
                cashFlowReportCollectionList.add(cashFlowReport);
                template.updateFirst(FinanceDataHelper.findById(statement.getId()), FinanceDataHelper.updateReconcileIndicatorDefination, StatementModel.class, userAccount.getAccountStatementCollectionName());
                template.insert(cashFlowReportCollectionList, userAccount.getAccountReportCollectionName());
            }
        }
    }

    private static CashFlowReportCollection getCashFlowReportCollection(AccountCollection userAccount, BankAccountStatementModel statement) {
        CashFlowReportCollection cashFlowReport = new CashFlowReportCollection();
        cashFlowReport.setAccountDocumentId(userAccount.getId());
        cashFlowReport.setAccountStatementModelId(statement.getId());
        cashFlowReport.setTransactionDate(statement.getC_transactionDate());
        cashFlowReport.setDescription(statement.getC_description());
        cashFlowReport.setType(statement.getC_type());
        if (statement.getC_type().equalsIgnoreCase("Cr.")) {
            cashFlowReport.setAmmount(statement.getC_credit());
            cashFlowReport.setCategory_i(Category_I.IN);
        } else {
            cashFlowReport.setAmmount(statement.getC_debit());
            cashFlowReport.setCategory_i(Category_I.OUT);
        }
        cashFlowReport.setYear(statement.getC_transactionDate().getYear());
        return cashFlowReport;
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
                template.updateFirst(FinanceDataHelper.findById(statement.getId()), FinanceDataHelper.updateReconcileIndicatorDefination, StatementModel.class, userAccount.getAccountStatementCollectionName());
            }
        }
        List<ReportCollection> holdingList = template.findAll(ReportCollection.class, userAccount.getAccountReportCollectionName());
        log.info("Holding updated...");
    }
}
