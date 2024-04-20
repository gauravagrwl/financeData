package org.gauravagrwl.financeData.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.AccountTypeEnum;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.BankAccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.InvestmentCryptoAccountStatement;
import org.gauravagrwl.financeData.model.profileAccount.statementCollection.InvestmentStockAccountStatement;
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

        List<? extends AccountStatementDocument> statementDocumentList;

        switch (accountCollection.getInstitutionCategory()) {
            case BANKING -> statementDocumentList = processBankStatement(file, accountCollection);
            case INVESTMENT -> statementDocumentList = processInvestmentStatement(file, accountCollection);
            case LOAN -> statementDocumentList = processLoanStatement(file, accountCollection);
            case ASSETS -> statementDocumentList = processAssetsStatement(file, accountCollection);
            default -> throw new FinanceDataException("No mapping strategy defined.");
        }

        accountDocumentService.saveAccountStatementDocuments(statementDocumentList, accountCollection);
        financeDataSyncService.calculateUpdateAccountStatement(accountCollection);
        return ResponseEntity.ok("Account statement updated for account id : " + accountId);
    }

    private List<? extends AccountStatementDocument> processAssetsStatement(MultipartFile file, AccountCollection accountCollection) {
        return null;
    }

    private List<? extends AccountStatementDocument> processLoanStatement(MultipartFile file, AccountCollection accountCollection) {
        return null;
    }

    private List<? extends AccountStatementDocument> processInvestmentStatement(MultipartFile file, AccountCollection accountCollection) throws IOException {
        List<? extends AccountStatementDocument> statementList = new ArrayList<>();

        if (AccountTypeEnum.STOCK.compareTo(accountCollection.getAccountType()) == 0) {
            MappingStrategy<InvestmentStockAccountStatement> headerColumnNameMappingStrategy = (MappingStrategy<InvestmentStockAccountStatement>) accountCollection.getHeaderColumnNameMappingStrategy(accountCollection.getCsvProfile());
            InputStreamReader reader = new InputStreamReader(file.getInputStream());

            CsvToBean<InvestmentStockAccountStatement> csvToBean = new CsvToBeanBuilder<InvestmentStockAccountStatement>(
                    reader)
                    .withProfile(accountCollection.getCsvProfile())
                    .withSeparator(',').withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(headerColumnNameMappingStrategy)
                    .build();

            List<InvestmentStockAccountStatement> transactionList = new ArrayList<>();
            csvToBean.iterator().forEachRemaining(transactionList::add);
            statementList = transactionList;
        } else {
            MappingStrategy<InvestmentCryptoAccountStatement> headerColumnNameMappingStrategy = (MappingStrategy<InvestmentCryptoAccountStatement>) accountCollection.getHeaderColumnNameMappingStrategy(accountCollection.getCsvProfile());
            InputStreamReader reader = new InputStreamReader(file.getInputStream());

            CsvToBean<InvestmentCryptoAccountStatement> csvToBean = new CsvToBeanBuilder<InvestmentCryptoAccountStatement>(
                    reader)
                    .withProfile(accountCollection.getCsvProfile())
                    .withSeparator(',').withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(headerColumnNameMappingStrategy)
                    .build();

            List<InvestmentCryptoAccountStatement> transactionList = new ArrayList<>();
            csvToBean.iterator().forEachRemaining(transactionList::add);
            statementList = transactionList;
        }
        return statementList;
    }

    private List<BankAccountStatementDocument> processBankStatement(MultipartFile file, AccountCollection accountCollection) throws IOException {
        MappingStrategy<BankAccountStatementDocument> headerColumnNameMappingStrategy = (MappingStrategy<BankAccountStatementDocument>) accountCollection.getHeaderColumnNameMappingStrategy(accountCollection.getCsvProfile());

        InputStreamReader reader = new InputStreamReader(file.getInputStream());

        CsvToBean<BankAccountStatementDocument> csvToBean = new CsvToBeanBuilder<BankAccountStatementDocument>(
                reader)
                .withProfile(accountCollection.getCsvProfile())
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
            transDoc.setAccountDocumentId(accountCollection.getId());
        });
        return transactionList;
    }

}
