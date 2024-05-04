package org.gauravagrwl.financeData.model.accountTransStatement.investment.cryptoInvestment;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import org.gauravagrwl.financeData.helper.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.CsvDateTimeToUTCDateTimeConverter;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;
import org.gauravagrwl.financeData.model.statementModel.StockInvestmentAccountStatementModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CryptoAppAccountStatementTransaction extends CryptoInvestmentAccountStatementTransaction {

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Timestamp (UTC)", converter = CsvDateTimeToUTCDateTimeConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private LocalDateTime timestamp; // common in crypto and coinbase

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Description", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String transaction_description;

    @CsvBindByNames({
            @CsvBindByName(column = "Currency", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String crypto_currency;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal amount;

    @CsvBindByNames({
            @CsvBindByName(column = "To Currency", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String to_Currency;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "To Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal to_Amount;

    @CsvBindByNames({
            @CsvBindByName(column = "Native Currency", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String native_Currency;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Native Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal native_Amount;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Native Amount (in USD)", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal native_Amount_USD;

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Kind", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String transaction_kind;

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Hash", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String transaction_hash;

    @Override
    public List<StatementModel> updateAccountStatement(AccountCollection accountCollection) {
        List<StatementModel> statementModelList = new ArrayList<>();
        StockInvestmentAccountStatementModel statementModel = new StockInvestmentAccountStatementModel();
        setAccountDocumentId(accountCollection.getId());
        statementModel.setAccountId(accountCollection.getId());
        statementModel.setAccountStatementId(getId());


        statementModelList.add(statementModel);
        return statementModelList;
    }
}
