package org.gauravagrwl.financeData.model.userAccounts.transactions.investmentTransactions;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.helper.FinanceAppHelper;
import org.gauravagrwl.financeData.helper.converters.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.converters.CsvMDYDateStringToDateConverter;
import org.gauravagrwl.financeData.helper.enums.TransactionType;
import org.gauravagrwl.financeData.model.userAccounts.statements.AccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.statements.InvestmentAccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class RobinhoodStockAccountTransaction extends AccountTransaction {
    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Activity Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate activityDate;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Process Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate processDate;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Settle Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate settleDate;

    @CsvBindByNames({
            @CsvBindByName(column = "Instrument", profiles = {
                    "Robinhood_STOCK"}),})
    private String instrument;

    @CsvBindByNames({
            @CsvBindByName(column = "Description", profiles = {
                    "Robinhood_STOCK"}),})
    private String description;

    @CsvBindByNames({
            @CsvBindByName(column = "Trans Code", profiles = {
                    "Robinhood_STOCK"}),})
    private String transCode;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Quantity", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal quantity;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Price", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal price;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal amount;

    @Getter
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'accountTransactionId':?#{#self._id} }", collection = "InvestmentAccountStatement")
    List<AccountStatement> accountStatements;

    @Override
    public AccountStatement transformToStatement() {
        InvestmentAccountStatement statement = new InvestmentAccountStatement();

        statement.getAccountTransactionIds().add(getId());
        statement.setAccountId(getUserAccountId());

        statement.setTransactionDate(ZonedDateTime.of(activityDate, LocalTime.NOON, ZoneId.of("UTC")));
        statement.setInstrument(instrument);
        statement.setDescription(description);
        statement.setQuantity(quantity);
        statement.setRate(price);
        statement.setAmount(amount.abs().setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));

        if (price.compareTo(BigDecimal.ZERO) != 0) {
            if (StringUtils.equalsAnyIgnoreCase(transCode, "STO", "BTC")) {
                statement.setFee((amount.abs().subtract(quantity.multiply(price).multiply(BigDecimal.valueOf(100)))).abs().setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
            } else {
                statement.setFee((amount.abs().subtract(quantity.multiply(price))).abs().setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
            }
        }
        switch (transCode) {
            case "Buy" -> statement.setTransactionType(TransactionType.BUY);
            case "Sell" -> statement.setTransactionType(TransactionType.SELL);
            case "STO" -> statement.setTransactionType(TransactionType.OSTO);
            case "ACH" -> {
                if (StringUtils.containsIgnoreCase(description, "Deposit")) {
                    statement.setTransactionType(TransactionType.DEPOSIT);
                } else if (StringUtils.containsIgnoreCase(description, "Withdrawal")) {
                    statement.setTransactionType(TransactionType.WITHDRAWAL);
                } else if (StringUtils.containsIgnoreCase(description, "Cancel")) {
                    statement.setTransactionType(TransactionType.FAILED);
                }
            }
            case "CDIV", "SLIP" -> statement.setTransactionType(TransactionType.EARN);
            case "REC" -> statement.setTransactionType(TransactionType.EARN);
            case "OEXP", "OASGN" -> statement.setTransactionType(TransactionType.OTHERS);
            case "BTC" -> statement.setTransactionType(TransactionType.OBTC);
            case "DTAX" -> statement.setTransactionType(TransactionType.CHARGES);
            case "OCA" -> statement.setTransactionType(TransactionType.CANCEL);
            case "MISC" -> {
                if (BigDecimal.ZERO.compareTo(price) > 0) {
                    statement.setTransactionType(TransactionType.EARN);
                } else {
                    statement.setTransactionType(TransactionType.OTHERS);
                }
            }
            default -> throw new FinanceAppException("No Switch statement defined for : %s", transCode);
        }


        return statement;
    }

    @Override
    public List<AccountStatement> transformToStatementList() {
        {
            InvestmentAccountStatement statement = new InvestmentAccountStatement();
            statement.getAccountTransactionIds().add(getId());
            statement.setAccountId(getUserAccountId());
            statement.setCurrency(getCurrency());

            statement.setTransactionDate(ZonedDateTime.of(activityDate, LocalTime.NOON, ZoneId.of("UTC")));
            statement.setInstrument(instrument);
            statement.setDescription(description);
            statement.setQuantity(quantity);
            statement.setRate(price);
            statement.setAmount(amount.abs().setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));

            if (price.compareTo(BigDecimal.ZERO) != 0) {
                if (StringUtils.equalsAnyIgnoreCase(transCode, "STO", "BTC")) {
                    statement.setFee((amount.abs().subtract(quantity.multiply(price).multiply(BigDecimal.valueOf(100)))).abs().setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
                } else {
                    statement.setFee((amount.abs().subtract(quantity.multiply(price))).abs().setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
                }
            }
            statement.setTransactionType(determineTransactionType(transCode));
            return List.of(statement);
        }
    }

    private TransactionType determineTransactionType(String transCode) {
        TransactionType type = null;
        switch (transCode) {
//            case "BTC" -> type = TransactionType.BUY;
//            case "STO" -> type = TransactionType.BUY;
//            case "ACH" -> type = TransactionType.BUY;
//            case "Sell" -> type = TransactionType.BUY;
//            case "Buy" -> type = TransactionType.BUY;
//            case "OASGN" -> type = TransactionType.BUY;
//            case "CDIV" -> type = TransactionType.BUY;
//            case "OEXP" -> type = TransactionType.BUY;
//            case "SLIP" -> type = TransactionType.BUY;
//            case "DTAX" -> type = TransactionType.BUY;
//            case "OCA" -> type = TransactionType.BUY;
//            case "MISC" -> type = TransactionType.BUY;
//            case "REC" -> type = TransactionType.BUY;


            case "Buy" -> type = TransactionType.BUY;
            case "Sell" -> type = TransactionType.SELL;
            case "STO" -> type = TransactionType.OSTO;
            case "ACH" -> {
                if (StringUtils.containsIgnoreCase(description, "Deposit")) {
                    type = TransactionType.DEPOSIT;
                } else if (StringUtils.containsIgnoreCase(description, "Withdrawal")) {
                    type = TransactionType.WITHDRAWAL;
                } else if (StringUtils.containsIgnoreCase(description, "Cancel")) {
                    type = TransactionType.FAILED;
                }
            }
            case "CDIV", "SLIP", "REC" -> type = TransactionType.EARN;
            case "OEXP", "OASGN" -> type = TransactionType.OTHERS;
            case "BTC" -> type = TransactionType.OBTC;
            case "DTAX" -> type = TransactionType.CHARGES;
            case "OCA" -> type = TransactionType.CANCEL;
            case "MISC" -> {
                if (BigDecimal.ZERO.compareTo(price) > 0) {
                    type = TransactionType.EARN;
                } else {
                    type = TransactionType.OTHERS;
                }
            }
            default -> throw new FinanceAppException("No Switch statement defined for : %s", transCode);
        }
        return type;
    }

}
