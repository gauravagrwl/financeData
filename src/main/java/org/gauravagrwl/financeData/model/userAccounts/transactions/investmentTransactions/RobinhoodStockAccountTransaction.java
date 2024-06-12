package org.gauravagrwl.financeData.model.userAccounts.transactions.investmentTransactions;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
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
    private AccountStatement accountStatement;

    @Override
    public AccountStatement transformToStatement() {
        InvestmentAccountStatement statement = new InvestmentAccountStatement();
        statement.setAccountTransactionId(getId());
        statement.setAccountId(getUserAccountId());

        statement.setTransactionDate(ZonedDateTime.of(activityDate, LocalTime.NOON, ZoneId.of("UTC")));
        statement.setInstrument(instrument);
        statement.setDescription(description);
        statement.setQuantity(quantity);
        statement.setPrice(price);
        statement.setAmount(amount.abs().setScale(2, RoundingMode.UP));

        if (price.compareTo(BigDecimal.ZERO) != 0) {
            if (StringUtils.equalsAnyIgnoreCase(transCode, "STO", "BTC")) {
                statement.setFee((amount.abs().subtract(quantity.multiply(price).multiply(BigDecimal.valueOf(100)))).abs().setScale(2, RoundingMode.UP));
            } else {
                statement.setFee((amount.abs().subtract(quantity.multiply(price))).abs().setScale(2, RoundingMode.UP));
            }
        }
        switch (transCode) {
            case "Buy" -> statement.setTransactionType(TransactionType.Buy);
            case "Sell" -> statement.setTransactionType(TransactionType.Sell);
            case "STO" -> statement.setTransactionType(TransactionType.OSTO);
            case "ACH" -> {
                if (StringUtils.containsIgnoreCase(description, "Deposit")) {
                    statement.setTransactionType(TransactionType.Deposit);
                } else if (StringUtils.containsIgnoreCase(description, "Withdrawal")) {
                    statement.setTransactionType(TransactionType.Withdrawal);
                } else if (StringUtils.containsIgnoreCase(description, "Cancel")) {
                    statement.setTransactionType(TransactionType.Failed);
                }
            }
            case "CDIV", "SLIP", "REC" -> statement.setTransactionType(TransactionType.Earn);
            case "OEXP" -> statement.setTransactionType(TransactionType.OEXP);
            case "OASGN" -> statement.setTransactionType(TransactionType.OASGN);
            case "BTC" -> statement.setTransactionType(TransactionType.OBTC);
            case "DTAX" -> statement.setTransactionType(TransactionType.TAX);
            case "OCA" -> statement.setTransactionType(TransactionType.Cancel);
            case "MISC" -> {
                if (BigDecimal.ZERO.compareTo(price) > 0) {
                    statement.setTransactionType(TransactionType.Earn);
                } else {
                    statement.setTransactionType(TransactionType.Charge);
                }
            }
            default -> throw new FinanceAppException("No Switch statement defined for : %s", transCode);
        }


        return statement;
    }

}
