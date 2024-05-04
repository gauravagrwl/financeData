package org.gauravagrwl.financeData.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.service.AccountService;
import org.gauravagrwl.financeData.service.AccountStatementDocumentService;
import org.gauravagrwl.financeData.service.FinanceDataCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(value = "/upload")
public class UploadController {

    private final AccountStatementDocumentService accountDocumentService;
    private final AccountService accountService;
    Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    FinanceDataCommonService financeDataSyncService;


    public UploadController(AccountStatementDocumentService accountDocumentService, AccountService accountService) {
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
        AccountCollection accountCollection = accountService.getAccountDocument(accountId, userName);

        List<AccountStatementTransaction> statementModelList = new ArrayList<>();

        MappingStrategy<? extends AccountStatementTransaction> headerColumnNameModelMappingStrategy = accountCollection.getHeaderColumnNameModelMappingStrategy();
        InputStreamReader reader = new InputStreamReader(file.getInputStream());

        CsvToBean<AccountStatementTransaction> csvToBean = new CsvToBeanBuilder<AccountStatementTransaction>(
                reader)
                .withProfile(accountCollection.getProfileType())
                .withSeparator(',').withIgnoreLeadingWhiteSpace(true)
                .withMappingStrategy(headerColumnNameModelMappingStrategy)
                .build();
        csvToBean.iterator().forEachRemaining(statementModelList::add);

//Insert Statment as it is..
        accountDocumentService.saveAccountStatementModelList(statementModelList, accountCollection);
        financeDataSyncService.calculateUpdateAccountStatement(accountCollection);
        financeDataSyncService.calculateUpdateAccountReport(accountCollection);
        return ResponseEntity.ok("Account statement updated for account id : " + accountId);
    }
}
