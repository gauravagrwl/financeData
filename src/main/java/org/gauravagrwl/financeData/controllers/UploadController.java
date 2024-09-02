package org.gauravagrwl.financeData.controllers;

import com.opencsv.bean.CsvToBean;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.gauravagrwl.financeData.services.AccountService;
import org.gauravagrwl.financeData.services.FinanceAppBatchService;
import org.gauravagrwl.financeData.services.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/uploadTransactions")
@Slf4j
@Tag(name = "Upload Transactions", description = "Upload account transaction document downloaded from Account Website.")
public class UploadController {

    @Autowired
    AccountService accountService;

    @Autowired
    StatementService statementService;

    @Autowired
    FinanceAppBatchService batchService;

    @PostMapping("/uploadAccountTransactions")
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

        statementService.insertAccountTransactions(transactionsList, userAccount);
        batchService.runSchedule();
        return ResponseEntity.ok("Account statement updated for account id : " + accountId);
    }
}
