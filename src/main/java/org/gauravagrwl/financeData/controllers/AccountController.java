package org.gauravagrwl.financeData.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.enums.AccountType;
import org.gauravagrwl.financeData.helper.enums.InstitutionCategory;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/accounts")
@Slf4j
@Tag(name = "Account Operations")
public class AccountController {

    @Autowired
    AccountService accountService;

    //TODO: 1. POST Add Single Account for a given user.
    //TODO: 1.1 POST Add List of Accounts for a given user.
    @PostMapping(value = "/addAccount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> addAccount(@RequestParam(required = true) String username,
                                             @RequestBody UserAccount userAccount) {
        String userAccountId = accountService.addUserAccount(username, userAccount);
        return ResponseEntity.ok("Account Added with Id: " + userAccountId);
    }

    @PostMapping(value = "/addAccounts", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> addAccounts(@RequestParam(required = true) String username,
                                                           @RequestBody List<UserAccount> userAccounts) {
        Map<String, String> result = new HashMap<>();
        userAccounts.forEach(userAccount -> {
            try {
                String userAccountId = accountService.addUserAccount(username, userAccount);
                result.put(userAccount.getAccountNumber(), "Account Added with Id: " + userAccountId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.put(userAccount.getAccountNumber(), e.getMessage());
            }
        });
        return ResponseEntity.ok(result);
    }


    //TODO: 2. GET List of Accounts for a given user.
    //TODO: 2.1 GET Single Account for a given user by accountId.
    @GetMapping(value = "/getAccount", produces = "application/json")
    public ResponseEntity<?> getProfileAccount(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId) {
        UserAccount accountDocument = accountService.getAccountDetails(accountId, username);
        return ResponseEntity.ok(accountDocument);
    }

    @GetMapping(value = "/getAccounts", produces = "application/json")
    public ResponseEntity<?> getProfileAccounts(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "institutionCategory", required = false) InstitutionCategory instCategory,
            @RequestParam(name = "accountType", required = false) AccountType accountType) {
        List<UserAccount> userAccounts = accountService.getUserAccounts(username);
        if (null != instCategory) {
            userAccounts = userAccounts.stream().filter(a -> a.getInstitutionCategory().compareTo(instCategory) == 0).collect(Collectors.toList());
        }
        if (null != accountType) {
            userAccounts = userAccounts.stream().filter(a -> a.getAccountType().compareTo(accountType) == 0).collect(Collectors.toList());
        }
        return ResponseEntity.ok(userAccounts);
    }

    //TODO: 3. PUT modify Account for a given user.
    //TODO: 4. POST DELETE Account for a given user.
}
