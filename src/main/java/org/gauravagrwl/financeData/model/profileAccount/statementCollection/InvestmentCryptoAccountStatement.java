package org.gauravagrwl.financeData.model.profileAccount.statementCollection;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentCryptoAccountStatement extends AccountStatementDocument {
    // Robinhood: Activity Date Process Date Settle Date Instrument Description
    // Trans Code Quantity Price Amount

    // Crypto: Timestamp (UTC), Transaction Description, Currency, Amount, To
    // Currency,
    // To Amount, Native Currency, Native Amount, Native Amount (in USD),
    // Transaction
    // Kind, Transaction Hash,
    // Coinbase: Timestamp, Transaction Type, Asset, Quantity Transacted, Spot Price
    // Currency, Spot Price at Transaction, Subtotal, Total (inclusive of fees
    // and/or
    // spread), Fees and/or Spread, Notes
//profile: Coinbase_CRYPTO  CryptoApp_CRYPTO
//    @CsvBindByNames({
//            @CsvBindByName(column = "Description", profiles = {
//                    "Robinhood_STOCK"}),})
//    @CsvCustomBindByNames({
//            @CsvCustomBindByName(column = "Settle Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
//                    "Robinhood_STOCK"}),})

    @CsvBindByNames({
            @CsvBindByName(column = "Timestamp", profiles = {
                    "Coinbase_CRYPTO"}),})
    private LocalDateTime timestamp; // common in crypto and coinbase

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Type", profiles = {
                    "Coinbase_CRYPTO"}),})
    private String transactionType;

    @CsvBindByNames({
            @CsvBindByName(column = "Asset", profiles = {
                    "Coinbase_CRYPTO"}),})
    private String crypto_instrument;

    @CsvBindByNames({
            @CsvBindByName(column = "Quantity Transacted", profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal quantity;

    @CsvBindByNames({
            @CsvBindByName(column = "Spot Price at Transaction", profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal rate;

    @CsvBindByNames({
            @CsvBindByName(column = "Total (inclusive of fees and/or spread)", profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal amount;

    @CsvBindByNames({
            @CsvBindByName(column = "Fees and/or Spread", profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal fee;

    @CsvBindByNames({
            @CsvBindByName(column = "Notes", profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal description;

    @Override
    public String findKeyName() {
        return crypto_instrument;
    }

//    private String cryptoapp_transaction_description;
//    private String cryptoapp_from_crypto_Coin;
//    private BigDecimal cryptoapp_from_crypto_Coin_qty;
//    private String cryptoapp_to_crypto_Coin;
//    private BigDecimal cryptoapp_to_crypto_Coin_qty;
//    private Currency cryptoapp_nativeCurrency = Currency.getInstance("USD");
//    private BigDecimal cryptoapp_currency_amount;
//    private String cryptoapp_transaction_kind;
//    private String cryptoapp_transaction_hash;
}
