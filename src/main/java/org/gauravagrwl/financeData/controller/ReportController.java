package org.gauravagrwl.financeData.controller;

import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.enums.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountReportsModel.ReportCollection;
import org.gauravagrwl.financeData.service.AccountService;
import org.gauravagrwl.financeData.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/report")
public class ReportController {

    ReportService reportService;

    @Autowired
    AccountService accountService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/getCashFlowReport")
    public ResponseEntity<?> getCashFlowReportYear(@RequestParam(name = "userName", required = true) String userName,
                                                   @RequestParam(name = "currency", required = false) String currency,
                                                   @RequestParam(name = "institutionCategory", required = false) InstitutionCategoryEnum instCategory) {
        {
            List<AccountCollection> userAccounts = accountService.getUserAccounts(userName);
            List<AccountCollection> accountCollections = new ArrayList<>();

            if (StringUtils.isBlank(currency)) {
                accountCollections = userAccounts.stream().filter(a -> a.getInstitutionCategory().compareTo(instCategory) == 0).collect(Collectors.toList());
            } else {
                accountCollections = userAccounts.stream().filter(a -> (a.getInstitutionCategory().compareTo(instCategory) == 0)
                        && a.getInstitutionCurrency().getCurrencyCode().equalsIgnoreCase(currency)).collect(Collectors.toList());
            }
            if (accountCollections.isEmpty()) {
                throw new FinanceDataException("No Accounts found for the user: " + userName);
            }
            List<ReportCollection> cashFlowReport = new ArrayList<>();
            for (AccountCollection invAccount : accountCollections) {
                cashFlowReport.addAll(reportService.getAccountReports(invAccount.getAccountReportCollectionName()));
            }
            return ResponseEntity.ok(cashFlowReport);
        }
    }

    @GetMapping("/getUserHoldings")
    public ResponseEntity<?> getUserHoldings(@RequestParam(required = true) String userName,
                                             @RequestParam(required = true) String accountId) {
        List<AccountCollection> userAccounts = accountService.getUserAccounts(userName);
        List<AccountCollection> accountCollections = userAccounts.stream()
                .filter(a -> a.getInstitutionCategory()
                        .compareTo(InstitutionCategoryEnum.INVESTMENT) == 0)
                .filter(a -> a.getId().equalsIgnoreCase(accountId))
                .collect(Collectors.toList());
        List<ReportCollection> holdingList = new ArrayList<>();
        for (AccountCollection invAccount : accountCollections) {
            holdingList.addAll(reportService.getAccountReports(invAccount.getAccountReportCollectionName()));
        }
        return ResponseEntity.ok(holdingList);
    }
//
//    @GetMapping("/getAssetsReport")
//    public ResponseEntity<?> getAssetsReport(@RequestParam(required = true) String userName) {
//        List<AccountCollection> userAccounts = accountService.getUserAccounts(userName);
//        List<AccountCollection> accountCollections = userAccounts.stream().filter(a -> a.getInstitutionCategory().compareTo(InstitutionCategoryEnum.ASSETS) == 0).collect(Collectors.toList());
//        List<ReportCollection> assetsReports = new ArrayList<>();
//        for (AccountCollection invAccount : accountCollections) {
//            assetsReports.addAll(reportService.getAccountReports(invAccount.getAccountReportCollectionName()));
//        }
//        return ResponseEntity.ok(assetsReports);
//    }

}

