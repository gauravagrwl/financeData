package org.gauravagrwl.financeData.model.userAccounts.accounts;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.gauravagrwl.financeData.model.userAccounts.transactions.bankTransactions.ChaseBankingAccountTransaction;

import java.io.InputStreamReader;
import java.math.BigDecimal;

@Getter
@Setter
public class BankAccount extends UserAccount {

    private BigDecimal accountBalance = BigDecimal.ZERO.setScale(2);

    @NotBlank(message = "Account holding type is required. Ex: Individual / Joint")
    private String holdingType;

    @NotBlank(message = "Account code is required.")
    private String accountCode;

    @NotBlank(message = "Account code is type is required. Ex: IIFC or Routing")
    private String accountCodeType;

    public CsvToBean<AccountTransaction> getCsvTransactionMapperToBean(InputStreamReader reader) {
        switch (getProfileType()) {
            case "Chase_CHK", "Chase_SAV" -> {
                MappingStrategy<ChaseBankingAccountTransaction> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<ChaseBankingAccountTransaction>()
                        .withForceCorrectRecordLength(true).build();
                headerColumnNameMappingStrategy.setProfile(getProfileType());
                headerColumnNameMappingStrategy.setType(ChaseBankingAccountTransaction.class);
                return new CsvToBeanBuilder<AccountTransaction>(
                        reader)
                        .withProfile(getProfileType())
                        .withSeparator(',').withIgnoreLeadingWhiteSpace(true)
                        .withFilter(strings -> {
                            // If first field is not date do not process that line.
                            if (strings[0].length() < 4) {
                                return false;
                            }
                            return true;
                        })
                        .withMappingStrategy(headerColumnNameMappingStrategy)
                        .build();
            }
            default -> throw new FinanceAppException("No mapper defined for the profile: %s", getProfileType());
        }
    }
}
