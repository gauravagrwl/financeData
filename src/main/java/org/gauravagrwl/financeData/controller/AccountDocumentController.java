package org.gauravagrwl.financeData.controller;

import java.util.List;

import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.service.AccountDocumentService;
import org.gauravagrwl.financeData.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/accountStatement")
@Slf4j
public class AccountDocumentController {

    @Autowired
    AccountDocumentService accountDocumentService;

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
        return ResponseEntity.ok("Document is not removed.");

    }
}