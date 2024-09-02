package org.gauravagrwl.financeData.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/accountLedger")
@Slf4j
@Tag(name = "Ledger Operations", description = "Ledger operation. Ledger statement same as downloaded from Account site. No changes done. Normally added from Uplod only.")
public class LedgerController {

    /**
     * Get Account Ledger Statements of an account.
     *
     * @param username
     * @param accountId
     * @return
     */
    @GetMapping(value = "/getLedgerStatements", produces = "application/json")
    public ResponseEntity<?> getLedgerStatements(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId) {
        return ResponseEntity.ok("getLedgerStatement");
    }

    /**
     * Ge all duplicate ledger statements of an account.
     *
     * @param username
     * @param accountId
     * @param ind
     * @return
     */

    @GetMapping(value = "/getDuplicateLedgerRecords", produces = "application/json")
    public ResponseEntity<?> getLedgerStatement(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "isDuplicate", required = true) Boolean ind
    ) {
        return ResponseEntity.ok("getLedgerStatement");
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
    @GetMapping(value = "/filterLedgerRecords", produces = "application/json")
    public ResponseEntity<?> getFilterLedgerStatement(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "filters", required = false) Map<String, String> filters) {
        return ResponseEntity.ok("filterLedgerRecords");
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
    @DeleteMapping(value = "/deleteAccountLedger", produces = "application/json")
    public ResponseEntity<?> deleteAccountLedgerStatement(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "accountId", required = true) String accountId,
            @RequestParam(name = "statementId", required = true) List statementIds) {
        return ResponseEntity.ok("deleteAccountLedger");
    }

}
