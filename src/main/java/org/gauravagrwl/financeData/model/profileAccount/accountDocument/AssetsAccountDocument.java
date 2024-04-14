package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import java.math.BigDecimal;

import lombok.*;
import org.springframework.data.mongodb.core.query.Update;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssetsAccountDocument extends AccountDocument {

    // Total amount for the purchase.
    private BigDecimal amountInvestment;

    // Asset name
    private String propertyName;

    // Asset acquired Year
    private String acquiredYear;

    private boolean isRentable;

    private String tenantName;

    private String tenantNumber;

    @Override
    public BigDecimal getAccountStatementBalance() {
        return getAmountInvestment();
    }

    @Override
    public Update getUpdateBalanceUpdateQuery(BigDecimal amount) {
        return Update.update("amountInvestment", amount);

    }

    @Override
    public BigDecimal calculateAccountBalance() {
        return null;
    }

}
