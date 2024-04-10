package org.gauravagrwl.financeData.controller;

import org.gauravagrwl.financeData.model.documents.userAccount.AccountDocument;
import org.gauravagrwl.financeData.service.UserAccountService;
import org.gauravagrwl.financeData.utility.FinanceDataHelper;
import org.gauravagrwl.financeData.utility.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.utility.InstitutionTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/profileAccounts")
public class UserAccountController {
    Logger LOGGER = LoggerFactory.getLogger(UserAccountController.class);

    UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping(value = "/addAccount")
    ResponseEntity<String> addUserAccount(@RequestParam(required = true) String userName,
                                          AccountDocument accountDocument){

        return ResponseEntity.ok("Account Added with Id: ");
    }
    @PostMapping(value = "/addAccounts", consumes = "application/json", produces = "application/json")
    ResponseEntity<?> addUserAccounts(@RequestParam(required = true) String userName,
                                      List<AccountDocument> accountDocuments){
        Map<String, String> result = new HashMap<>();
        accountDocuments.forEach(accountDocument -> {
        try {
            String userAccountId = userAccountService.addUserAccount(accountDocument, userName);
            result.put(accountDocument.getAccountNumber(), "Account Added with Id: " + userAccountId);
        } catch (Exception e) {
            result.put(accountDocument.getAccountNumber(), "Account already exist.");
        }
        });
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/getAccount",  produces = "application/json")
    ResponseEntity<?> getUserAccount(@RequestParam(required = true) String userName,
                                     @RequestParam(required = true) String accountId){
        AccountDocument accountDocument = userAccountService.getUserAccountDocument(accountId, userName);
        return ResponseEntity.ok(accountDocument);
    }

    @GetMapping(value = "/getAccounts",  produces = "application/json")
    ResponseEntity<?> getUserAccounts(@RequestParam(required = true) String userName,@RequestParam(name = "institutionCategory", required = false) InstitutionCategoryEnum instCategory,
                                      @RequestParam(name = "accountType", required = false) InstitutionTypeEnum accountType) {
        List<AccountDocument> userAccounts = userAccountService.getUserAccounts(userName);
        userAccounts.forEach(account -> account
                .setAccountNumber(FinanceDataHelper.prependAccountNumber(account.getAccountNumber())));
        return ResponseEntity.ok(userAccounts);
    }

    @DeleteMapping(value = "/deleteAccount")
    ResponseEntity<String> deleteUserAccount(@RequestParam(required = true) String userName, @RequestParam(required = true)
    String accountId){
        AccountDocument accountDocument = userAccountService.deleteUserAccountDocument(accountId, userName);
        return null;
    }
    @PatchMapping(value = "/toggleAccountStatus")
    ResponseEntity<String> toggleAccountActiveStatus(@RequestParam(required = true) String userName,
                                                     @RequestParam(required = true)
                                                     String accountId){
        userAccountService.toggleAccountActiveStatus(userName, accountId);
        return ResponseEntity.ok("Account is toggled!");
    }

    @GetMapping(value = "/getInstitutionType")
    ResponseEntity<List<String>> getAccountType(
            @RequestParam(name = "userName", required = false) String userName,
            @RequestParam(name = "accountCategory", required = false) String accountCategory) {

        List<String> accountTypeList = new ArrayList<>();
        for (InstitutionTypeEnum accountType : InstitutionTypeEnum.values()) {
            accountTypeList.add(accountType.name());
        }
        return ResponseEntity.ok(accountTypeList);
    }

    @GetMapping(value = "/getInstitutionCategory")
    ResponseEntity<List<String>> getAccountCategory(@RequestParam(name = "userName", required = false) String userName) {
        List<String> accountCatList = new ArrayList<>();
        for (InstitutionCategoryEnum accountCat : InstitutionCategoryEnum.values()) {
            accountCatList.add(accountCat.getCategoryName());
        }
        return ResponseEntity.ok(accountCatList);
    }

}
