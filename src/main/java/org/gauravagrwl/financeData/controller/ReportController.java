package org.gauravagrwl.financeData.controller;

import org.gauravagrwl.financeData.service.CashFlowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/report")
public class ReportController {

    CashFlowService cashFlowService;

    public ReportController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    @GetMapping("/getCashFlowReportYeaer")
    public ResponseEntity<?> getCashFlowReportYeaer(@RequestParam(required = false) String userName) {
        return ResponseEntity.ok("");
    }

}
