package org.gauravagrwl.financeData.controller;

import java.util.ArrayList;
import java.util.List;

import org.gauravagrwl.financeData.helper.AccountTypeEnum;
import org.gauravagrwl.financeData.helper.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.service.FinanceAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(value = "/financeApp")
@Slf4j
public class FinanceAppController {

    FinanceAppService financeAppService;

    public FinanceAppController(FinanceAppService financeAppService) {
        this.financeAppService = financeAppService;
    }

    /**
     * 
     * @return
     */

    @DeleteMapping(path = "dropAllCollections")
    ResponseEntity<?> dropAllCollection() {
        log.warn("All collections are dropped.");
        financeAppService.dropAllCollections();
        return ResponseEntity.ok("All Collection is dropped.");
    }

    /**
     * 
     * @param collectionName
     * @return
     */
    @DeleteMapping(path = "dropCollection")
    ResponseEntity<?> dropCollection(@RequestParam String collectionName) {
        financeAppService.dropCollection(collectionName);
        log.warn("All collections are dropped.");
        return ResponseEntity.ok("All Collection is dropped.");
    }

    /**
     * 
     * @return
     */
    @GetMapping(path = "/getCollections")
    public ResponseEntity<?> getCollections() {
        return ResponseEntity.ok(financeAppService.getAllCollections());
    }

    /**
     * 
     * @param userName
     * @param accountCategory
     * @return
     */
    @GetMapping(value = "/getAccountsType")
    ResponseEntity<List<String>> getAccountType(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountCategory", required = false) String accountCategory) {

        List<String> accountTypeList = new ArrayList<>();
        for (AccountTypeEnum accountType : AccountTypeEnum.values()) {
            accountTypeList.add(accountType.name().toUpperCase());
        }
        return ResponseEntity.ok(accountTypeList);
    }

    /**
     * 
     * @return
     */
    @GetMapping(value = "/getInstitutionCategory")
    ResponseEntity<List<String>> getAccountCategory() {
        List<String> accountCatList = new ArrayList<>();
        for (InstitutionCategoryEnum accountCat : InstitutionCategoryEnum.values()) {
            accountCatList.add(accountCat.getCategoryName().toUpperCase());
        }
        return ResponseEntity.ok(accountCatList);
    }

}
