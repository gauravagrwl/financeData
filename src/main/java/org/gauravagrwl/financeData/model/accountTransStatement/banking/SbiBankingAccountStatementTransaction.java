package org.gauravagrwl.financeData.model.accountTransStatement.banking;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import org.gauravagrwl.financeData.helper.converters.CSVDMYStringToDateConverter;
import org.gauravagrwl.financeData.helper.converters.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountStatementModel.BankAccountStatementModel;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SbiBankingAccountStatementTransaction extends AccountStatementTransaction {
    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Txn Date", converter = CSVDMYStringToDateConverter.class, profiles = {
                    "SBI_SAV"}),})
    private LocalDate s_Txn_Date; // Date Of Transactions private LocalDate postingDate; // Date Of Posting

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Value Date", converter = CSVDMYStringToDateConverter.class, profiles = {
                    "SBI_SAV"}),})
    private LocalDate s_Value_Date; // Date Of Transactions private LocalDate postingDate; // Date Of Posting


    @CsvBindByNames({@CsvBindByName(column = "Description", profiles = {"SBI_SAV"}),})
    private String s_Description; // If Dr., Cr.

    @CsvBindByNames({@CsvBindByName(column = "Ref No./Cheque No.", profiles = {"SBI_SAV"}),})
    private String s_Reference; // If Dr., Cr.

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Debit", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "SBI_SAV"}),})
    private BigDecimal s_Debit = BigDecimal.ZERO;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Credit", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "SBI_SAV"}),})
    private BigDecimal s_Credit = BigDecimal.ZERO;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Balance", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "SBI_SAV"}),})
    private BigDecimal s_Balance = BigDecimal.ZERO;

    public List<StatementModel> updateAccountStatement(AccountCollection accountCollection) {
        List<StatementModel> statementModelList = new ArrayList<>();
        BankAccountStatementModel statementModel = new BankAccountStatementModel();
        statementModel.setAccountTransactionId(getId());
        statementModel.setAccountId(accountCollection.getId());
        statementModel.setC_transactionDate(s_Value_Date);
        statementModel.setC_description(s_Description);
        statementModel.setC_notes(s_Reference);
        statementModel.setDuplicate(getDuplicate());
        if (BigDecimal.ZERO.compareTo(s_Debit) == 0) {
            statementModel.setC_type("Cr.");
            statementModel.setC_credit(s_Credit.abs().setScale(2));
        } else {
            statementModel.setC_type("Dr.");
            statementModel.setC_debit(s_Debit.abs().setScale(2));
        }
        statementModelList.add(statementModel);
        return statementModelList;
    }


}
