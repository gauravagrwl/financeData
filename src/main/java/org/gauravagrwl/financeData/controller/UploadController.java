package org.gauravagrwl.financeData.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.BankAccountStatementDocument;
import org.gauravagrwl.financeData.service.AccountDocumentService;
import org.gauravagrwl.financeData.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;

@RestController
@RequestMapping(value = "/upload")
public class UploadController {

    Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    private final AccountDocumentService accountDocumentService;

    private final AccountService accountService;

    public UploadController(AccountDocumentService accountDocumentService, AccountService accountService) {
        this.accountDocumentService = accountDocumentService;
        this.accountService = accountService;
    }

    @PostMapping("/uploadDocuments")
    public ResponseEntity<String> uploadAccountStatement(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(required = true, name = "file") MultipartFile file) throws IOException {

        if ((file.isEmpty()) || (!accountService.isUserAccountExist(accountId, userName))) {
            return ResponseEntity.badRequest().body("No Account exist for this user.");
        }
        AccountDocument accountDocument = accountService.getAccountDocument(accountId, userName);

        String profileType = accountDocument.getAccountType().getAccountTypeName();

        MappingStrategy<BankAccountStatementDocument> headerColumnNameMappingStrategy = BankAccountStatementDocument
                .getHeaderColumnNameMappingStrategy(
                        profileType);

        InputStreamReader reader = new InputStreamReader(file.getInputStream());
        CsvToBean<BankAccountStatementDocument> csvToBean = new CsvToBeanBuilder<BankAccountStatementDocument>(
                reader)
                .withProfile(accountDocument.getAccountType().getAccountTypeName())
                .withSeparator(',').withIgnoreLeadingWhiteSpace(true)
                .withMappingStrategy(headerColumnNameMappingStrategy)
                .build();
        List<BankAccountStatementDocument> transactionList = new ArrayList<>();
        csvToBean.iterator().forEachRemaining(transactionList::add);
        transactionList.forEach(transDoc -> {
            if (StringUtils.equalsIgnoreCase("Credit", transDoc.getType())) {
                transDoc.setCredit(transDoc.getTransient_amount().abs());
            } else {
                transDoc.setDebit(transDoc.getTransient_amount().abs());
            }
            transDoc.setAccountDocumentId(accountDocument.getId());
        });

        accountDocumentService.saveAccountStatementDocuments(transactionList, accountDocument);

        return ResponseEntity.ok("Account statement updated for account id : " + accountId);
    }

}
