package org.gauravagrwl.financeData.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.AccountTypeEnum;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.helper.InstitutionCategoryEnum;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/profileAccount")
@Slf4j
@Tag(name = "Account Operations")
public class AccountController {

    AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * @param userName
     * @param accountCollection
     * @return
     */
    @PostMapping(value = "/addAccount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> addAccount(@RequestParam(required = true) String userName,
                                             @RequestBody AccountCollection accountCollection) {
        String userAccountId = accountService.addUserAccount(accountCollection, userName);
        return ResponseEntity.ok("Account Added with Id: " + userAccountId);
    }

    /**
     * @param userName
     * @param accountCollections
     * @return
     */
    @PostMapping(value = "/addAccounts", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> addAccounts(@RequestParam(required = true) String userName,
                                                           @RequestBody List<AccountCollection> accountCollections) {
        Map<String, String> result = new HashMap<>();
        accountCollections.forEach(accountDocument -> {
            try {
                String userAccountId = accountService.addUserAccount(accountDocument, userName);
                result.put(accountDocument.getAccountNumber(), "Account Added with Id: " + userAccountId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.put(accountDocument.getAccountNumber(), "Account already exist.");
            }
        });
        return ResponseEntity.ok(result);
    }

    /**
     * @param userName
     * @param accountId
     * @return
     */
    @GetMapping(value = "/getAccount", produces = "application/json")
    public ResponseEntity<AccountCollection> getProfileAccount(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId) {
        AccountCollection accountCollection = accountService.getAccountDocument(accountId, userName);
        return ResponseEntity.ok(accountCollection);
    }

    /**
     * @param userName
     * @param instCategory
     * @param accountType
     * @return
     */
    @GetMapping(value = "/getAccounts", produces = "application/json")
    public ResponseEntity<List<AccountCollection>> getProfileAccounts(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "institutionCategory", required = false) InstitutionCategoryEnum instCategory,
            @RequestParam(name = "accountType", required = false) AccountTypeEnum accountType) {
        List<AccountCollection> userAccounts = accountService.getUserAccounts(userName);
        userAccounts.forEach(account -> account
                .setAccountNumber(FinanceDataHelper.getAccountDisplayNumber(account.getAccountNumber())));
        return ResponseEntity.ok(userAccounts);
    }

    /**
     * @param userName
     * @param accountId
     * @return
     */
    @PatchMapping(value = "/toggleAccount")
    public ResponseEntity<String> toggleAccount(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId) {
        accountService.toggleAccount(userName, accountId);
        return ResponseEntity.ok("Account is toggled!");
    }

    @DeleteMapping(value = "/deleteAccount")
    public ResponseEntity<?> deleteProfileAccount(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "accountId", required = true) String accountId) {
        accountService.deleteProfileAccount(accountId, userName);
        return ResponseEntity.ok("Account Deleted.");
    }

}
