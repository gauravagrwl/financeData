package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import java.math.BigDecimal;

import lombok.*;

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
    public void calculate(BigDecimal amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculate'");
    }

}
