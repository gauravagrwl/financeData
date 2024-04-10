package org.gauravagrwl.financeData.controller;

import org.gauravagrwl.financeData.service.FinanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/financeController")
public class FinanceDataController {
    private final FinanceDataService financeDataService;

    public FinanceDataController(FinanceDataService financialDataService) {
        this.financeDataService = financialDataService;
    }

    Logger LOGGER = LoggerFactory.getLogger(FinanceDataController.class);

    @DeleteMapping(path = "/dropAllCollections")
    public ResponseEntity<?> dropAllCollections(){
        LOGGER.warn("Dropping all Collections");
        financeDataService.dropAllCollection();
        return ResponseEntity.ok(financeDataService.dropAllCollection());
    }

    @DeleteMapping(path = "/dropCollection")
    public ResponseEntity<?>dropCollection(@RequestParam String collectionName) {
        LOGGER.warn(collectionName + "Will be dropped.");

        return ResponseEntity.ok(financeDataService.dropCollection(collectionName));
    }

    @GetMapping(path = "/getCollections")
    public ResponseEntity<?>getCollection() {
        return ResponseEntity.ok(financeDataService.getAllCollections());
    }
}
