package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.InvestmentCryptoAccountStatement;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.InvestmentStockAccountStatement;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;

/**
 * This class supports below Institution Sub Category:
 * <p>
 * STOCK(InstitutionCategoryEnum.INVESTMENT, "STOCK", "201"),
 * CRYPTO(InstitutionCategoryEnum.INVESTMENT, "CRYPTO", "202"),
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
    public MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(String mappingProfile) {
        if (StringUtils.containsIgnoreCase(mappingProfile, "Stock")) {
            MappingStrategy<InvestmentStockAccountStatement> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<InvestmentStockAccountStatement>()
                    .withForceCorrectRecordLength(true).build();
            headerColumnNameMappingStrategy.setProfile(mappingProfile);
            headerColumnNameMappingStrategy.setType(InvestmentStockAccountStatement.class);
            return headerColumnNameMappingStrategy;
        } else {
            MappingStrategy<InvestmentCryptoAccountStatement> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<InvestmentCryptoAccountStatement>()
                    .withForceCorrectRecordLength(true).build();
            headerColumnNameMappingStrategy.setProfile(mappingProfile);
            headerColumnNameMappingStrategy.setType(InvestmentCryptoAccountStatement.class);
            return headerColumnNameMappingStrategy;
        }
    }

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
