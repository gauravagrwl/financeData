package org.gauravagrwl.financeData.model.accountTransStatement.banking;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.helper.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.CsvMDYDateStringToDateConverter;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.statementModel.BankAccountStatementModel;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ChaseBankingAccountStatementTransaction extends AccountStatementTransaction {
    // Chase - chk / sav: Details Posting Date Description Amount Type Balance Check
    // or Slip #
    // AMEX: Date Description Amount Extended Details Appears On Your Statement As
    // Address City/State Zip Code Country Reference Category


    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Posting Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Chase_SAV", "Chase_CHK"}),})
    private LocalDate s_posting_Date; // Date Of Transactions private LocalDate postingDate; // Date Of Posting

    @CsvBindByNames({@CsvBindByName, @CsvBindByName(column = "Details", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String s_details; // If Dr., Cr.

    @CsvBindByNames({@CsvBindByName, @CsvBindByName(column = "Description", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String s_descriptions; // Descriptions

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Chase_SAV", "Chase_CHK"}),})
    private BigDecimal s_amount = BigDecimal.ZERO;

    @CsvBindByNames({@CsvBindByName(column = "Type", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String s_type;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Balance", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Chase_SAV", "Chase_CHK"}),})
    private BigDecimal s_balance = BigDecimal.ZERO;

    @CsvBindByNames({@CsvBindByName(column = "Check or Slip #", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String s_check_Slip; // Descriptions

//    public static Comparator<ChaseBankingAccountStatementModel> sortBankStatment = Comparator
//            .comparing(ChaseBankingAccountStatementModel::getS_posting_Date)
//            .thenComparing(ChaseBankingAccountStatementModel::getC_type);


    @Override
    public List<StatementModel> updateAccountStatement(AccountCollection accountCollection) {
        List<StatementModel> statementModelList = new ArrayList<>();
        setAccountDocumentId(accountCollection.getId());
        BankAccountStatementModel statementModel = new BankAccountStatementModel();
        statementModel.setAccountStatementId(getId());
        statementModel.setAccountId(accountCollection.getId());
        statementModel.setC_transactionDate(s_posting_Date);
        statementModel.setC_description(s_descriptions);
        statementModel.setC_notes(s_check_Slip);
        statementModel.setDuplicate(getDuplicate());
        if (StringUtils.equalsIgnoreCase(s_details, "Credit")) {
            statementModel.setC_type("Cr.");
            statementModel.setC_credit(s_amount.abs().setScale(2));
        } else {
            statementModel.setC_type("Dr.");
            statementModel.setC_credit(s_amount.abs().setScale(2));
        }
        statementModel.setStatement(this);

        statementModelList.add(statementModel);
        return statementModelList;
    }


    // public static Comparator<BankAccountStatementDocument>
    // sortBankStatmentBySerialNumber = Comparator
    // .comparingLong(BankAccountStatementDocument::getSno);
}
// SAV - debit goes in to the account
// SAV - Credit goes out of the account
// CRE - debits goes out of the account
// CRE - Credit goes in the account

