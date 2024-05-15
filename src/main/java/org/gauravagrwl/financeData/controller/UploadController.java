package org.gauravagrwl.financeData.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.service.AccountService;
import org.gauravagrwl.financeData.service.AccountStatementModelService;
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

    private final AccountStatementModelService accountTransactionService;

    private final AccountService accountService;
    Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    public UploadController(AccountStatementModelService accountTransactionService, AccountService accountService, FinanceDataCommonService financeDataSyncService) {
        this.accountTransactionService = accountTransactionService;
        this.accountService = accountService;
        this.financeDataSyncService = financeDataSyncService;
    }

    @Autowired
    FinanceDataCommonService financeDataSyncService;


    @PostMapping("/uploadDocuments")
    public ResponseEntity<String> uploadAccountStatement(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(required = true, name = "file") MultipartFile file) throws IOException {

        if ((file.isEmpty()) || (!accountService.isUserAccountExist(accountId, userName))) {
            return ResponseEntity.badRequest().body("No Account exist for this user.");
        }
        AccountCollection accountCollection = accountService.getAccountDetails(accountId, userName);

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
        accountTransactionService.saveAccountStatementModelList(statementModelList, accountCollection);
        financeDataSyncService.calculateUpdateAccountStatement(accountCollection);
        financeDataSyncService.calculateUpdateAccountReport(accountCollection);
        return ResponseEntity.ok("Account statement updated for account id : " + accountId);
    }
}
