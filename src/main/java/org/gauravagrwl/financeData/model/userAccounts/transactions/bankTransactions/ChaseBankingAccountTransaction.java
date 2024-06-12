package org.gauravagrwl.financeData.model.userAccounts.transactions.bankTransactions;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.helper.converters.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.converters.CsvMDYDateStringToDateConverter;
import org.gauravagrwl.financeData.model.userAccounts.statements.AccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.statements.BankAccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChaseBankingAccountTransaction extends AccountTransaction {
    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Posting Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Chase_SAV", "Chase_CHK"}),})
    private LocalDate postingDate; // Date Of Transactions private LocalDate postingDate; // Date Of Posting

    @CsvBindByNames({@CsvBindByName, @CsvBindByName(column = "Details", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String details; // If Dr., Cr.

    @CsvBindByNames({@CsvBindByName, @CsvBindByName(column = "Description", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String description; // Descriptions

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Chase_SAV", "Chase_CHK"}),})
    private BigDecimal amount = BigDecimal.ZERO;

    @CsvBindByNames({@CsvBindByName(column = "Type", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String type;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Balance", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Chase_SAV", "Chase_CHK"}),})
    private BigDecimal balance = BigDecimal.ZERO;

    @CsvBindByNames({@CsvBindByName(column = "Check or Slip #", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String checkOrSlip; // Descriptions

    @Getter
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'accountTransactionId':?#{#self._id} }", collection = "BankAccountStatement")
    private AccountStatement accountStatement;

    @Override
    public AccountStatement transformToStatement() {
        BankAccountStatement statement = new BankAccountStatement();
        statement.setAccountTransactionId(getId());
        statement.setAccountId(getUserAccountId());

        statement.setTransactionDate(postingDate);
        statement.setAmount(amount.abs().setScale(2));
        if (StringUtils.isNotBlank(checkOrSlip))
            statement.setDescription(StringUtils.join(description, "---", checkOrSlip));
        else
            statement.setDescription(description);
        if (StringUtils.equalsIgnoreCase(details, "Credit")) {
            statement.setType("Cr.");
        } else {
            statement.setType("Dr.");
        }
        return statement;
    }


}
