package org.gauravagrwl.financeData.controller;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.service.AccountDocumentService;
import org.gauravagrwl.financeData.service.AccountService;
import org.gauravagrwl.financeData.service.FinanceDataSyncService;
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
    AccountDocumentService accountDocumentService;

    @Autowired
    FinanceDataSyncService financeDataSyncService;

    @Autowired
    AccountService accountService;

    @GetMapping(value = "/getAccountStatement", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAccountStatement(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId) {
        AccountDocument accountDocument = accountService.getAccountDocument(accountId, userName);
        List<? extends AccountStatementDocument> accountStatementDocuments = accountDocumentService
                .getAccountStatementDocuments(accountDocument);
        return ResponseEntity.ok(accountStatementDocuments);
    }

    @DeleteMapping(value = "/deleteTransaction")
    public ResponseEntity<String> deleteAccountTransaction(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "transactionId", required = true) String statementId) {
        AccountDocument accountDocument = accountService.getAccountDocument(accountId, userName);
        accountDocumentService.deleteAccountStatementDocument(accountDocument,
                statementId);
        financeDataSyncService.calculateAccountBalance(accountDocument);
        return ResponseEntity.ok("Document is not removed.");

    }
}