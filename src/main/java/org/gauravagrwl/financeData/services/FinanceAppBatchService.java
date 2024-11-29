package org.gauravagrwl.financeData.services;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.helper.FinanceAppQuery;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.model.userAccounts.statements.AccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FinanceAppBatchService {

    @Autowired
    MongoTemplate template;
    @Autowired
    BankAccountService bankAccountService;

    @Autowired
    InvestmentAccountService investmentAccountService;


    @Scheduled(fixedRate = 6000000)
    public void runSchedule() {
        List<UserAccount> userAccountList = template.find(FinanceAppQuery.findAllDataQuery(), UserAccount.class);
        for (UserAccount userAccount : userAccountList) {
            if (userAccount.getStartTransactionProcessing()) {
                log.info("Processing Account Transactions for account: {} ", userAccount.getAccountDisplayName());
                startProcessing(userAccount);
                template.updateFirst(FinanceAppQuery.findByIdQuery(userAccount.getId()), FinanceAppQuery.updateBooleanValueIndicator("StartTransactionProcessing", Boolean.FALSE), UserAccount.class);
            } else {
                log.info("Account Transactions process is complete for account: {}", userAccount.getAccountDisplayName());
            }

        }
    }

    void startProcessing(UserAccount userAccount) {
        log.info("Updating account statement for {} and Account: {}", userAccount.getInstitutionCategory(), userAccount.getAccountDisplayName());
        insertAccountStatementModel(userAccount);

        switch (userAccount.getInstitutionCategory()) {
            case BANKING -> {
                log.info("Updating account Balance for Bank Account: {}", userAccount.getAccountDisplayName());
                bankAccountService.updateBankAccountStatementBalance(userAccount);
                log.info("Identify duplicate records from statement for Bank Account: {}", userAccount.getAccountDisplayName());
                bankAccountService.updateDuplicateBankRecords(userAccount);
                log.info("Updating account Report for Bank Account: {}", userAccount.getAccountDisplayName());
                bankAccountService.insertBankAccountReportStatement(userAccount);
            }
            case INVESTMENT -> {
                log.info("Updating account Balance for Investment Account: {}", userAccount.getAccountDisplayName());
                investmentAccountService.updateInvestmentAccountDetails(userAccount);
                log.info("Identify duplicate records from statement for Investment Account: {}", userAccount.getAccountDisplayName());
                investmentAccountService.updateDuplicateInvestmentRecords(userAccount);
                log.info("Updating account Report for Investment Account: {}", userAccount.getAccountDisplayName());
                investmentAccountService.insertInvestmentAccountReportStatement(userAccount);
            }
            default ->
                    throw new FinanceAppException("No Switch statement defined for : {}", userAccount.getInstitutionCategory().getCategoryName());
        }
    }

    /**
     * Transform Transaction to Model
     *
     * @param userAccount
     */
    public void insertAccountStatementModel(UserAccount userAccount) {
        List<AccountTransaction> accountTransactionList = template.find(FinanceAppQuery.sortAllAccountTransactionQuery(userAccount), AccountTransaction.class, userAccount.getAccountTransactionCollectionName());
//        List<AccountTransaction> accountTransactionList = template.findAll(AccountTransaction.class, userAccount.getAccountTransactionCollectionName());
        List<AccountTransaction> accountTransactionForProcessingList = accountTransactionList.stream()
                .filter(
                        statementTransaction -> (statementTransaction.getAccountStatements().isEmpty())
                ).toList();

        List<AccountStatement> accountStatementList = new ArrayList<>();
        for (AccountTransaction accountTransaction : accountTransactionForProcessingList) {
//            AccountStatement statement = accountTransaction.transformToStatement();
            accountStatementList.addAll(accountTransaction.transformToStatementList());
        }
        accountStatementList.forEach(s -> log.info("Account Statement inserted for {} with id {}", userAccount.getAccountDisplayName(), template.insert(s)));
    }
}
