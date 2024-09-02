package org.gauravagrwl.financeData.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accountSummary")
@Slf4j
@Tag(name = "Summary Operations", description = "Returns Account reports for all kind of account type")
public class AccountSummaryController {
    //1. CashFlow report
    //2. AccountHoldings
    //3. Balance Sheet

    @GetMapping(value = "/getCashFlowReport", produces = "application/json")
    public ResponseEntity<?> getCashFlowReport(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "year", required = false) String year) {
        return ResponseEntity.ok("filterStatementRecords");
    }

    @GetMapping(value = "/getInvestmentHoldings", produces = "application/json")
    public ResponseEntity<?> getInvestmentHoldings(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = false) String accountId) {
        return ResponseEntity.ok("getInvestmentHoldings");
    }

    @GetMapping(value = "/getAssetsReturns", produces = "application/json")
    public ResponseEntity<?> getAssetsReturns(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = false) String accountId) {
        return ResponseEntity.ok("getInvestmentHoldings");
    }

    @GetMapping(value = "/getBalanceSheet", produces = "application/json")
    public ResponseEntity<?> getBalanceSheet(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = false) String accountId) {
        return ResponseEntity.ok("getBalanceSheet");
    }


}
