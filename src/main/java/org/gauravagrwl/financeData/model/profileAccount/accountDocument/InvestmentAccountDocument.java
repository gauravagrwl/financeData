package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import java.math.BigDecimal;

import lombok.*;
import org.springframework.data.mongodb.core.query.Update;

/**
 * This class supports below Institution Sub Category:
 * 
 * STOCK(InstitutionCategoryEnum.INVESTMENT, "STOCK", "201"),
 * CRYPTO(InstitutionCategoryEnum.INVESTMENT, "CRYPTO", "202"),
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class InvestmentAccountDocument extends AccountDocument {

    // Total Amount Invested.
    private BigDecimal amountInvestment = BigDecimal.ZERO;

    // Total Amount Returned.
    private BigDecimal amountReturn = BigDecimal.ZERO;

    // Is this Account Auto Tradeable.
    private Boolean isAutoTradable = Boolean.FALSE;

    private String ledgerTransactionCollectionName;

    @Override
    public Update getUpdateBalanceUpdateQuery(BigDecimal amount) {
        return Update.update("amountInvestment", amount);
    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return getAmountInvestment();
    }

    @Override
    public BigDecimal calculateAccountBalance() {
        return null;
    }

}
