package org.gauravagrwl.financeData.model.accountTransStatement.investment;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.converters.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.converters.CsvDateTimeToUTCDateTimeConverter;
import org.gauravagrwl.financeData.helper.enums.TransactionKindEnum;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountStatementModel.StockInvestmentAccountStatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class CoinbaseAccountStatementTransaction extends AccountStatementTransaction {

    @CsvBindByNames({
            @CsvBindByName(column = "ID", profiles = {
                    "Coinbase_CRYPTO"}),})
    private String s_coinbase_ID;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Timestamp", converter = CsvDateTimeToUTCDateTimeConverter.class, profiles = {
                    "Coinbase_CRYPTO"}),})
    private ZonedDateTime s_timestamp;

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Type", profiles = {
                    "Coinbase_CRYPTO"}),})
    private String s_transaction_Type;

    @CsvBindByNames({
            @CsvBindByName(column = "Asset", profiles = {
                    "Coinbase_CRYPTO"}),})
    private String s_asset;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Quantity Transacted", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal s_quantity;

    @CsvBindByNames({
            @CsvBindByName(column = "Price Currency", profiles = {
                    "Coinbase_CRYPTO"}),})
    private String s_price_currency;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Price at Transaction", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal s_price_at_transaction;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Subtotal", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Coinbase_CRYPTO"}),})

    private BigDecimal s_subtotal;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Total (inclusive of fees and/or spread)", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal s_total_inclusive_fee;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Fees and/or Spread", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Coinbase_CRYPTO"}),})
    private BigDecimal s_fee;

    @CsvBindByNames({
            @CsvBindByName(column = "Notes", profiles = {
                    "Coinbase_CRYPTO"}),})
    private String s_notes;

    /**
     * Buy: instrument(assets / coin) added under any transaction code.
     * Sell: instrument(assets / coin) removed or sold under any transaction code.
     * O_*: Option transactions
     * Transfer: instrument(assets / coin) is transfer
     * Earn: Div, REC, SLIP, MISC
     * Charge:DTAX,
     */
    @Override
    public List<StatementModel> updateAccountStatement(AccountCollection accountCollection) {
        log.info(toString());
        List<StatementModel> statementModelList = new ArrayList<>();
        StockInvestmentAccountStatementModel sm = new StockInvestmentAccountStatementModel();

        sm.setAccountId(accountCollection.getId());
        sm.setAccountTransactionId(getId());

        sm.setC_transaction_dateTime(s_timestamp);
        sm.setC_instrument(s_asset);
        sm.setC_quantity(s_quantity);
        sm.setC_price(s_price_at_transaction);
        sm.setC_amount(s_total_inclusive_fee);
        sm.setC_fee(s_fee);
        sm.setC_description(s_notes);


        switch (s_transaction_Type) {
            case "Staking Income", "Learning Reward", "Receive" -> {
                sm.setC_trans_code(TransactionKindEnum.Earn);
            }
            case "Buy" -> {
                String[] tos = s_notes.split("for");
                String[] secondTransaction = tos[1].trim().split(" ");
                String new_instrument = secondTransaction[1];
                sm.setC_trans_code(TransactionKindEnum.Buy);
                BigDecimal purchase_quantity = new BigDecimal(secondTransaction[0]);
                if (StringUtils.equalsAnyIgnoreCase(new_instrument, "USD")) {
                    StockInvestmentAccountStatementModel ca = new StockInvestmentAccountStatementModel();
                    ca.setAccountId(accountCollection.getId());
                    ca.setC_transaction_dateTime(s_timestamp);
                    ca.setC_description(s_notes);
                    ca.setAccountTransactionId(getId());
                    ca.setC_amount(purchase_quantity);
                    ca.setC_trans_code(TransactionKindEnum.Deposit);
                    statementModelList.add(ca);
                }
            }
            case "Advance Trade Buy" -> {
                sm.setC_trans_code(TransactionKindEnum.Buy);


            }
            case "Advance Trade Sell" -> {
                sm.setC_trans_code(TransactionKindEnum.Sell);
            }
            case "Convert" -> {
                log.info(sm.toString());
                String[] tos = s_notes.split("to");
                String[] secondTransaction = tos[1].trim().split(" ");
                String new_instrument = secondTransaction[1];
                BigDecimal purchase_quantity = new BigDecimal(secondTransaction[0]);

                sm.setC_trans_code(TransactionKindEnum.Sell);
                StockInvestmentAccountStatementModel ca = new StockInvestmentAccountStatementModel();
                ca.setAccountId(accountCollection.getId());
                ca.setAccountTransactionId(getId());
                ca.setC_trans_code(TransactionKindEnum.Buy);
                ca.setC_transaction_dateTime(s_timestamp);
                ca.setC_instrument(new_instrument);
                ca.setC_quantity(purchase_quantity);
                ca.setC_price(s_total_inclusive_fee.divide(purchase_quantity, MathContext.DECIMAL64));
                ca.setC_amount(s_total_inclusive_fee);
                ca.setC_fee(BigDecimal.ZERO);
                ca.setC_description(s_notes);
                statementModelList.add(ca);
            }
            case "Send" -> {
                sm.setC_trans_code(TransactionKindEnum.Send);
            }
            case "Deposit" -> {
                sm.setC_trans_code(TransactionKindEnum.Deposit);
            }

            default -> {
                log.error("Transaction Code not define: " + toString());
                throw new FinanceDataException("Process fail");
            }
        }

        statementModelList.add(sm);
        return statementModelList;
    }

    @Override
    public String toString() {
        return "CoinbaseAccountStatementModel{" +
                "s_coinbase_ID='" + s_coinbase_ID + '\'' +
                ", s_timestamp=" + s_timestamp +
                ", s_transaction_Type='" + s_transaction_Type + '\'' +
                ", s_asset='" + s_asset + '\'' +
                ", s_quantity=" + s_quantity +
                ", s_price_currency='" + s_price_currency + '\'' +
                ", s_price_at_transaction=" + s_price_at_transaction +
                ", s_subtotal=" + s_subtotal +
                ", s_total_inclusive_fee=" + s_total_inclusive_fee +
                ", s_fee=" + s_fee +
                ", s_notes='" + s_notes + '\'' +
                '}';
    }
}
