package org.gauravagrwl.financeData.controller;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.service.AccountService;
import org.gauravagrwl.financeData.service.AccountStatementDocumentService;
import org.gauravagrwl.financeData.service.FinanceDataCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/accountStatement")
@Slf4j
public class AccountDocumentController {

    @Autowired
    AccountStatementDocumentService accountDocumentService;

    @Autowired
    FinanceDataCommonService financeDataSyncService;

    @Autowired
    AccountService accountService;

    @GetMapping(value = "/getAccountStatement", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAccountStatement(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId) {
        AccountCollection accountCollection = accountService.getAccountDocument(accountId, userName);
        List<AccountStatementTransaction> accountStatementDocuments = accountDocumentService
                .getAccountTransactionStatementDocuments(accountCollection);
        return ResponseEntity.ok(accountStatementDocuments);
    }

    @DeleteMapping(value = "/deleteTransaction")
    public ResponseEntity<String> deleteAccountTransaction(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "transactionId", required = true) String statementId) {
        AccountCollection accountCollection = accountService.getAccountDocument(accountId, userName);
        accountDocumentService.deleteAccountStatementDocument(accountCollection,
                statementId);
        financeDataSyncService.calculateUpdateAccountStatement(accountCollection);
        return ResponseEntity.ok("Document is not removed.");

    }
}