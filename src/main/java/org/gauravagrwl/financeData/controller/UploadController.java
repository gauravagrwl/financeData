package org.gauravagrwl.financeData.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.AccountTypeEnum;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.BankAccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.InvestmentCryptoAccountStatement;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.InvestmentStockAccountStatement;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/upload")
public class UploadController {

    private final AccountDocumentService accountDocumentService;
    private final AccountService accountService;
    Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

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

        String profileType = accountDocument.getCsvProfile();

        List<? extends AccountStatementDocument> statementDocumentList = new ArrayList<>();

        switch (accountDocument.getInstitutionCategory()) {
            case BANKING -> statementDocumentList = processBankStatement(file, accountDocument);
            case INVESTMENT -> statementDocumentList = processInvestmentStatement(file, accountDocument);
            case LOAN -> statementDocumentList = processLoanStatement(file, accountDocument);
            case ASSETS -> statementDocumentList = processAssetsStatement(file, accountDocument);
            default -> throw new FinanceDataException("No mapping strategy defined.");
        }

        accountDocumentService.saveAccountStatementDocuments(statementDocumentList, accountDocument);

        return ResponseEntity.ok("Account statement updated for account id : " + accountId);
    }

    private List<? extends AccountStatementDocument> processAssetsStatement(MultipartFile file, AccountDocument accountDocument) {
        return null;
    }

    private List<? extends AccountStatementDocument> processLoanStatement(MultipartFile file, AccountDocument accountDocument) {
        return null;
    }

    private List<? extends AccountStatementDocument> processInvestmentStatement(MultipartFile file, AccountDocument accountDocument) throws IOException {
        List<? extends AccountStatementDocument> statementList = new ArrayList<>();

        if (AccountTypeEnum.STOCK.compareTo(accountDocument.getAccountType()) == 0) {
            MappingStrategy<InvestmentStockAccountStatement> headerColumnNameMappingStrategy = (MappingStrategy<InvestmentStockAccountStatement>) accountDocument.getHeaderColumnNameMappingStrategy(accountDocument.getCsvProfile());
            InputStreamReader reader = new InputStreamReader(file.getInputStream());

            CsvToBean<InvestmentStockAccountStatement> csvToBean = new CsvToBeanBuilder<InvestmentStockAccountStatement>(
                    reader)
                    .withProfile(accountDocument.getCsvProfile())
                    .withSeparator(',').withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(headerColumnNameMappingStrategy)
                    .build();

            List<InvestmentStockAccountStatement> transactionList = new ArrayList<>();
            csvToBean.iterator().forEachRemaining(transactionList::add);
            statementList = transactionList;
        } else {
            MappingStrategy<InvestmentCryptoAccountStatement> headerColumnNameMappingStrategy = (MappingStrategy<InvestmentCryptoAccountStatement>) accountDocument.getHeaderColumnNameMappingStrategy(accountDocument.getCsvProfile());
            InputStreamReader reader = new InputStreamReader(file.getInputStream());

            CsvToBean<InvestmentCryptoAccountStatement> csvToBean = new CsvToBeanBuilder<InvestmentCryptoAccountStatement>(
                    reader)
                    .withProfile(accountDocument.getCsvProfile())
                    .withSeparator(',').withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(headerColumnNameMappingStrategy)
                    .build();

            List<InvestmentCryptoAccountStatement> transactionList = new ArrayList<>();
            csvToBean.iterator().forEachRemaining(transactionList::add);
            statementList = transactionList;
        }
        return statementList;
    }

    private List<BankAccountStatementDocument> processBankStatement(MultipartFile file, AccountDocument accountDocument) throws IOException {
        MappingStrategy<BankAccountStatementDocument> headerColumnNameMappingStrategy = (MappingStrategy<BankAccountStatementDocument>) accountDocument.getHeaderColumnNameMappingStrategy(accountDocument.getCsvProfile());

        InputStreamReader reader = new InputStreamReader(file.getInputStream());

        CsvToBean<BankAccountStatementDocument> csvToBean = new CsvToBeanBuilder<BankAccountStatementDocument>(
                reader)
                .withProfile(accountDocument.getCsvProfile())
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
        return transactionList;
    }

}
