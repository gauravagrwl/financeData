package org.gauravagrwl.financeData.model.userAccounts.accounts;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.gauravagrwl.financeData.model.userAccounts.transactions.investmentTransactions.CryptoAppAccountTransaction;
import org.gauravagrwl.financeData.model.userAccounts.transactions.investmentTransactions.RobinhoodStockAccountTransaction;

import java.io.InputStreamReader;
import java.math.BigDecimal;

@Getter
@Setter
@Slf4j
public class InvestmentAccount extends UserAccount {
    // Total amount invested in instruments
    private BigDecimal amountInvested = BigDecimal.ZERO.setScale(2);
    // Total amount returned in instruments
    private BigDecimal amountReturned = BigDecimal.ZERO.setScale(2);
    private BigDecimal netAmountProfitLoss = BigDecimal.ZERO.setScale(2);
    // Total Cash invested in Account
    private BigDecimal cashInvested = BigDecimal.ZERO.setScale(2);
    // Total Cash Returned in Account
    private BigDecimal cashReturn = BigDecimal.ZERO.setScale(2);
    private BigDecimal netCashProfitLoss = BigDecimal.ZERO.setScale(2);
    // Total Other Value returned in Account
    // In stock Option
    //In Crypto Stack amount
    private BigDecimal optionReturn = BigDecimal.ZERO.setScale(2);
    //In Crypto Stack amount
    private BigDecimal stakeReturn = BigDecimal.ZERO.setScale(2);
    //Other charges like Tax or other charges
    private BigDecimal otherCharges = BigDecimal.ZERO.setScale(2);

    public CsvToBean<AccountTransaction> getCsvTransactionMapperToBean(InputStreamReader reader) {
        switch (getProfileType()) {
            case "Robinhood_STOCK" -> {
                MappingStrategy<RobinhoodStockAccountTransaction> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<RobinhoodStockAccountTransaction>()
                        .withForceCorrectRecordLength(true).build();
                headerColumnNameMappingStrategy.setProfile(getProfileType());
                headerColumnNameMappingStrategy.setType(RobinhoodStockAccountTransaction.class);
                return new CsvToBeanBuilder<AccountTransaction>(
                        reader)
                        .withProfile(getProfileType())
                        .withSeparator(',').withIgnoreLeadingWhiteSpace(true).withIgnoreEmptyLine(Boolean.TRUE)
                        // If first field is not date do not process that line.
                        .withFilter(strings -> {
                            if (strings[0].length() < 4) {
                                return false;
                            }
                            return true;
                        }).withMappingStrategy(headerColumnNameMappingStrategy)
                        .build();
            }
            case "CryptoApp_CRYPTO" -> {
                MappingStrategy<CryptoAppAccountTransaction> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<CryptoAppAccountTransaction>()
                        .withForceCorrectRecordLength(true).build();
                headerColumnNameMappingStrategy.setProfile(getProfileType());
                headerColumnNameMappingStrategy.setType(CryptoAppAccountTransaction.class);
                return new CsvToBeanBuilder<AccountTransaction>(
                        reader)
                        .withProfile(getProfileType())
                        .withSeparator(',').withIgnoreLeadingWhiteSpace(true).withIgnoreEmptyLine(Boolean.TRUE)
                        // If first field is not date do not process that line.
                        .withFilter(strings -> {
                            if (strings[0].length() < 4) {
                                return false;
                            }
                            return true;
                        }).withMappingStrategy(headerColumnNameMappingStrategy)
                        .build();
            }
            default -> throw new FinanceAppException("No mapper defined for the profile: %s", getProfileType());
        }
    }
}
