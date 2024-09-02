package org.gauravagrwl.financeData.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.gauravagrwl.financeData.services.AccountService;
import org.gauravagrwl.financeData.services.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * User uploaded Account statements.
 */
@RestController
@RequestMapping(value = "/accountStatements")
@Slf4j
@Tag(name = "Statement Operations", description = "Statement operation that is stored in db after modified and stored. Not same as account transactions.")
public class StatementController {

    @Autowired
    AccountService accountService;

    @Autowired
    StatementService statementService;


    /**
     * Get Account Ledger Statements of an account.
     *
     * @param username
     * @param accountId
     * @return
     */
    @GetMapping(value = "/getAccountStatements", produces = "application/json")
    public ResponseEntity<?> getAccountStatements(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId) {
        return ResponseEntity.ok("getAccountStatements");
    }

    /**
     * Ge all duplicate ledger statements of an account.
     *
     * @param username
     * @param accountId
     * @param ind
     * @return
     */

    @GetMapping(value = "/getDuplicateStatementRecords", produces = "application/json")
    public ResponseEntity<?> getLedgerStatement(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "isDuplicate", required = true) Boolean ind
    ) {
        return ResponseEntity.ok("getDuplicateStatementRecords");
    }

    /**
     * Filter the ledger statement.
     *
     * @param username
     * @param accountId
     * @param filters
     * @return
     */
    @Operation(summary = "Filters the records by user need.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @GetMapping(value = "/filterStatementRecords", produces = "application/json")
    public ResponseEntity<?> getFilterLedgerStatement(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "filters", required = false) Map<String, String> filters) {
        return ResponseEntity.ok("filterStatementRecords");
    }

    /**
     * Delete ledger statement which user have selected.
     * Also delete it from Account statement.
     *
     * @param username
     * @param accountId
     * @param statementIds
     * @return
     */
    @DeleteMapping(value = "/deleteAccountStatements", produces = "application/json")
    public ResponseEntity<?> deleteAccountLedgerStatement(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "statementId", required = true) List statementIds) {
        return ResponseEntity.ok("deleteAccountStatements");
    }


    //TODO: 1. POST AccountTransaction for a given account.
    //TODO: 2. GET AccountTransaction for a given account.
    //TODO: 3. PUT modify AccountTransaction for a given account.
    //TODO: 4. POST DELETE AccountTransaction for a given account.


    @DeleteMapping("/deleteAccountTransaction")
    public ResponseEntity<String> deleteAccountTransaction(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "statementId", required = true) String statementId) {

        UserAccount userAccount = accountService.getAccountDetails(accountId, username);

        statementService.deleteAccountTransaction(userAccount, statementId);

        return ResponseEntity.ok(String.format("Account Statement Id: %s deleted for account id : %s", statementId, accountId));
    }

}
