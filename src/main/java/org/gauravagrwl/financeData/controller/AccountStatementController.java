package org.gauravagrwl.financeData.controller;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.service.AccountService;
import org.gauravagrwl.financeData.service.AccountStatementModelService;
import org.gauravagrwl.financeData.service.FinanceDataCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/accountStatement")
@Slf4j
public class AccountStatementController {

    @Autowired
    AccountStatementModelService statementModelService;

    @Autowired
    FinanceDataCommonService financeDataSyncService;

    @Autowired
    AccountService accountService;

    @GetMapping(value = "/getAccountTransactions", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAccountStatement(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId) {
        AccountCollection accountCollection = accountService.getAccountDetails(accountId, userName);
        List<AccountStatementTransaction> accountStatementDocuments = statementModelService
                .getAccountTransactionStatementDocuments(accountCollection);
        return ResponseEntity.ok(accountStatementDocuments);
    }

    @DeleteMapping(value = "/deleteStatement")
    public ResponseEntity<String> deleteAccountTransaction(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "transactionId", required = true) String statementId) {
        AccountCollection accountCollection = accountService.getAccountDetails(accountId, userName);
        statementModelService.deleteAccountStatementDocument(accountCollection,
                statementId);
        financeDataSyncService.calculateUpdateAccountStatement(accountCollection);
        return ResponseEntity.ok("Document is not removed.");

    }
}