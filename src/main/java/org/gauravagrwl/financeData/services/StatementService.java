package org.gauravagrwl.financeData.services;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.helper.FinanceAppQuery;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StatementService {
    @Autowired
    MongoTemplate template;

    @Autowired
    BankAccountService bankAccountService;

    public void insertAccountTransactions(List<AccountTransaction> transactionsList, UserAccount userAccount) {
        log.info("Saving account statement for account: " + userAccount.getAccountDisplayName());
        String accountTransactionCollectionName = userAccount.getAccountTransactionCollectionName();
        transactionsList.stream().forEach(accountTransaction -> {
            accountTransaction.setUserAccountId(userAccount.getId());
            accountTransaction.setCurrency(userAccount.getInstitutionCurrency());
            log.info("Account Transaction Inserted with id: {}", template.insert(accountTransaction, accountTransactionCollectionName).getId());
        });
        template.updateFirst(FinanceAppQuery.findByIdQuery(userAccount.getId()), FinanceAppQuery.updateBooleanValueIndicator("StartTransactionProcessing", Boolean.TRUE), UserAccount.class);
        log.info("Transaction Records update for account: {}", userAccount.getAccountDisplayName());
    }

    public void deleteAccountTransaction(UserAccount userAccount, String accountStatementId) {
        switch (userAccount.getInstitutionCategory()) {
            case BANKING -> {
                log.info("Deleting account transaction for Account: {}", userAccount.getAccountDisplayName());
                bankAccountService.deleteBankAccountStatement(userAccount, accountStatementId);
            }
            default ->
                    throw new FinanceAppException("No Switch statement defined for : {}", userAccount.getInstitutionCategory().getCategoryName());
        }
    }

}
