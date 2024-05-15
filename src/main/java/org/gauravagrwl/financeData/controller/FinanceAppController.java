package org.gauravagrwl.financeData.controller;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.enums.AccountTypeEnum;
import org.gauravagrwl.financeData.helper.enums.Category_I;
import org.gauravagrwl.financeData.helper.enums.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.service.FinanceAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/financeApp")
@Slf4j
public class FinanceAppController {

    FinanceAppService financeAppService;

    public FinanceAppController(FinanceAppService financeAppService) {
        this.financeAppService = financeAppService;
    }

    /**
     * @return
     */

    @DeleteMapping(path = "dropAllCollections")
    ResponseEntity<?> dropAllCollection() {
        log.warn("All collections are dropped.");
        financeAppService.dropAllCollections();
        return ResponseEntity.ok("All Collection is dropped.");
    }

    /**
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
     * @return
     */
    @GetMapping(path = "/getCollections")
    public ResponseEntity<?> getCollections() {
        return ResponseEntity.ok(financeAppService.getAllCollections());
    }

    /**
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

    //For Category
//    I -> Cash_out - DropDown
//    II -> Inv_OUT - DropDown
//    III -> Crypto - DropDown
//    IV -> Free form Text
//    V -> Amount

    /**
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

    @GetMapping(value = "/categoryI")
    ResponseEntity<?> getCategoryI() {
        List<String> catIlist = new ArrayList<>();
        for (Category_I c : Category_I.values()) {
            catIlist.add(c.getCategoryI());
        }
        return ResponseEntity.ok(catIlist);
    }

}
