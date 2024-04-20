package org.gauravagrwl.financeData.model.profileAccount.statementCollection;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.CsvMDYDateStringToDateConverter;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountStatementDocument extends AccountStatementDocument {
    // Chase - chk / sav: Details Posting Date Description Amount Type Balance Check
    // or Slip #
    // AMEX: Date Description Amount Extended Details Appears On Your Statement As
    // Address City/State Zip Code Country Reference Category

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Posting Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Chase_SAV", "Chase_CHK"}),})
    private LocalDate transactionDate; // Date Of Transactions private LocalDate postingDate; // Date Of Posting

    @CsvBindByNames({@CsvBindByName, @CsvBindByName(column = "Description", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String descriptions; // Descriptions

    @CsvBindByNames({@CsvBindByName, @CsvBindByName(column = "Details", profiles = {"Chase_SAV", "Chase_CHK"}),})
    private String type; // If Dr., Cr.

    private BigDecimal debit = BigDecimal.ZERO; // Amount
    private BigDecimal credit = BigDecimal.ZERO; // Amount
    private BigDecimal balance = BigDecimal.ZERO; // Need to be calculated

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Chase_SAV", "Chase_CHK"}),})
    @Transient
    private BigDecimal transient_amount = BigDecimal.ZERO;

    // private String notes; // contents memo, slip and category
    // private String category; // Chase credit card
    // Combined to one fields notes
    // private String memo; // Memo
    private String slip, referenceNo, notes;

    public static Comparator<BankAccountStatementDocument> sortBankStatment = Comparator
            .comparing(BankAccountStatementDocument::getTransactionDate)
            .thenComparing(BankAccountStatementDocument::getType);

    @Override
    public String findKeyName() {
        return null;
    }


    // public static Comparator<BankAccountStatementDocument>
    // sortBankStatmentBySerialNumber = Comparator
    // .comparingLong(BankAccountStatementDocument::getSno);
}
// SAV - debit goes in to the account
// SAV - Credit goes out of the account
// CRE - debits goes out of the account
// CRE - Credit goes in the account
