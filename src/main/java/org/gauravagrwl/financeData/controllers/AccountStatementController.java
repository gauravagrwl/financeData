package org.gauravagrwl.financeData.controllers;

import com.opencsv.bean.CsvToBean;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.gauravagrwl.financeData.services.AccountService;
import org.gauravagrwl.financeData.services.AccountStatementService;
import org.gauravagrwl.financeData.services.FinanceAppBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * User uploaded Account statements.
 */
@RestController
@RequestMapping(value = "/accountStatements")
@Slf4j
public class AccountStatementController {

    @Autowired
    AccountService accountService;

    @Autowired
    FinanceAppBatchService batchService;

    @Autowired
    AccountStatementService accountStatementService;

    //TODO: 1. POST AccountTransaction for a given account.
    //TODO: 2. GET AccountTransaction for a given account.
    //TODO: 3. PUT modify AccountTransaction for a given account.
    //TODO: 4. POST DELETE AccountTransaction for a given account.

    @PostMapping("/addAccountTransactions")
    public ResponseEntity<String> addAccountTransactions(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(required = true, name = "file") MultipartFile file) throws IOException {

        if ((file.isEmpty()) || (!accountService.accountExistForUser(accountId, username))) {
            return ResponseEntity.badRequest().body("No Account exist for this user.");
        }
        UserAccount userAccount = accountService.getAccountDetails(accountId, username);

        List<AccountTransaction> transactionsList = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(file.getInputStream());

        /*
        Get Mapping Strategy based on the account type to map upload statement to POJO.
         */
        CsvToBean<AccountTransaction> csvToBean = userAccount.getCsvTransactionMapperToBean(reader);
        csvToBean.iterator().forEachRemaining(transactionsList::add);

        accountStatementService.insertAccountTransactions(transactionsList, userAccount);
        batchService.runSchedule();
        return ResponseEntity.ok("Account statement updated for account id : " + accountId);
    }

    @DeleteMapping("/deleteAccountTransaction")
    public ResponseEntity<String> deleteAccountTransaction(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "statementId", required = true) String statementId) {

        UserAccount userAccount = accountService.getAccountDetails(accountId, username);

        accountStatementService.deleteAccountTransaction(userAccount, statementId);

        return ResponseEntity.ok(String.format("Account Statement Id: %s deleted for account id : %s", statementId, accountId));
    }


}
