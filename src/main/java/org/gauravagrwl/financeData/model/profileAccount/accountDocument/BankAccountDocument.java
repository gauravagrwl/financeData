package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.BankAccountStatementDocument;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;

/**
 * This class supports below Institution Sub Category:
 * <p>
 * CHECKING(InstitutionCategoryEnum.BANKING, "CHK", "101"),
 * SAVING(InstitutionCategoryEnum.BANKING, "SAV", "102"),
 * DEPOSIT(InstitutionCategoryEnum.BANKING, "DEP", "103"),
 * PPF(InstitutionCategoryEnum.BANKING, "PPF", "104"),
 * CREDIT(InstitutionCategoryEnum.BANKING, "CRE", "105"),
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Slf4j
public class BankAccountDocument extends AccountDocument {

    // Account Calculated Balance
    private BigDecimal accountBalance = BigDecimal.ZERO;

    // Account holding type
    private String holdingType;

    // Account Code: Routing code or IIFC code.
    private String accountCode;

    // Code Type: Routing or IIFC
    private String accountCodeType;

    @Override
    public MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(
            String mappingProfile) {
        MappingStrategy<BankAccountStatementDocument> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<BankAccountStatementDocument>()
                .withForceCorrectRecordLength(true).build();
        headerColumnNameMappingStrategy.setProfile(mappingProfile);
        headerColumnNameMappingStrategy.setType(BankAccountStatementDocument.class);
        return headerColumnNameMappingStrategy;
    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return getAccountBalance();
    }

    @Override
    public Update getUpdateBalanceUpdateQuery(BigDecimal amount) {
        return Update.update("accountBalance", amount);
    }

    @Override
    public BigDecimal calculateAccountBalance() {
        log.info("Calculate balance");
        return null;

    }

}
