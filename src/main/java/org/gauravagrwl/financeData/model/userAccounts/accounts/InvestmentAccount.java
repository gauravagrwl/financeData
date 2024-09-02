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
import org.gauravagrwl.financeData.model.userAccounts.transactions.investmentTransactions.RobinhoodStockAccountTransaction;

import java.io.InputStreamReader;
import java.math.BigDecimal;

@Getter
@Setter
@Slf4j
public class InvestmentAccount extends UserAccount {

    private BigDecimal cashInvested = BigDecimal.ZERO.setScale(2);
    private BigDecimal amountInvested = BigDecimal.ZERO.setScale(2);
    private BigDecimal optionReturn = BigDecimal.ZERO.setScale(2);

    private BigDecimal cashReturn = BigDecimal.ZERO.setScale(2);

    private BigDecimal amountReturn = BigDecimal.ZERO.setScale(2);

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
            default -> throw new FinanceAppException("No mapper defined for the profile: %s", getProfileType());
        }
    }
}
