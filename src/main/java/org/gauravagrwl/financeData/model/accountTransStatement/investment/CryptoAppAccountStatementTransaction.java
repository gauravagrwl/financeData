package org.gauravagrwl.financeData.model.accountTransStatement.investment;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.converters.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.converters.CsvDateTimeToUTCDateTimeConverter;
import org.gauravagrwl.financeData.helper.enums.TransactionKindEnum;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountStatementModel.StockInvestmentAccountStatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Slf4j
public class CryptoAppAccountStatementTransaction extends AccountStatementTransaction {

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Timestamp (UTC)", converter = CsvDateTimeToUTCDateTimeConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private ZonedDateTime s_timestamp; // common in crypto and coinbase

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Description", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String s_transaction_description;

    @CsvBindByNames({
            @CsvBindByName(column = "Currency", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String s_crypto_currency;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal s_amount;

    @CsvBindByNames({
            @CsvBindByName(column = "To Currency", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String s_to_Currency;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "To Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal s_to_Amount;

    @CsvBindByNames({
            @CsvBindByName(column = "Native Currency", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String s_native_Currency;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Native Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal s_native_Amount;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Native Amount (in USD)", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal s_native_Amount_USD;

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Kind", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String s_transaction_kind;

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Hash", profiles = {
                    "CryptoApp_CRYPTO"})})
    private String s_transaction_hash;

    @Override
    public List<StatementModel> updateAccountStatement(AccountCollection accountCollection) {
        List<StatementModel> statementModelList = new ArrayList<>();
        StockInvestmentAccountStatementModel sm = new StockInvestmentAccountStatementModel();
        sm.setAccountId(accountCollection.getId());
        sm.setAccountTransactionId(getId());
        sm.setC_transaction_dateTime(s_timestamp);
        sm.setC_description(s_transaction_description);
        switch (s_transaction_kind) {
            case "viban_purchase", "dust_conversion_credited" -> {
                sm.setC_instrument(s_to_Currency);
                sm.setC_quantity(s_to_Amount.abs());
                sm.setC_amount(s_native_Amount_USD);
                sm.setC_trans_code(TransactionKindEnum.Buy);
            }
            case "crypto_earn_program_created", "lockup_lock" -> {
                sm.setC_trans_code(TransactionKindEnum.Stake);
                sm.setC_instrument(s_crypto_currency);
                sm.setC_stake_quantity(s_amount.abs());
            }

            case "crypto_earn_program_withdrawn" -> {
                sm.setC_trans_code(TransactionKindEnum.StakeWithdrawl);
                sm.setC_instrument(s_crypto_currency);
                sm.setC_stake_quantity(s_amount.abs());
            }

            case "crypto_earn_interest_paid", "admin_wallet_credited", "crypto_earn_extra_interest_paid", "rewards_platform_deposit_credited", "mco_stake_reward", "referral_card_cashback",
                    "referral_gift", "reimbursement" -> {
                sm.setC_trans_code(TransactionKindEnum.Earn);
                sm.setC_instrument(s_crypto_currency);
                sm.setC_quantity(s_amount.abs());
                sm.setC_amount(s_native_Amount_USD);
            }
            case "crypto_exchange" -> {
                sm.setC_trans_code(TransactionKindEnum.Sell);
                sm.setC_instrument(s_crypto_currency);
                sm.setC_quantity(s_amount.abs());
                sm.setC_amount(s_native_Amount_USD);

                StockInvestmentAccountStatementModel bm = new StockInvestmentAccountStatementModel();
                bm.setAccountId(accountCollection.getId());
                bm.setAccountTransactionId(getId());
                bm.setC_transaction_dateTime(s_timestamp);
                bm.setC_description(s_transaction_description);
                bm.setC_trans_code(TransactionKindEnum.Buy);
                bm.setC_instrument(s_to_Currency);
                bm.setC_quantity(s_to_Amount.abs());
                bm.setC_amount(s_native_Amount_USD);
                statementModelList.add(bm);
            }
            case "card_cashback_reverted", "dust_conversion_debited", "reimbursement_reverted" -> {
                sm.setC_trans_code(TransactionKindEnum.Sell);
                sm.setC_instrument(s_crypto_currency);
                sm.setC_quantity(s_amount.abs());
                sm.setC_amount(s_native_Amount_USD);
            }
            case "crypto_wallet_swap_credited",
                    "crypto_wallet_swap_debited", "viban_deposit_precredit",
                    "viban_deposit_precredit_repayment" -> {
                sm.setC_instrument(s_crypto_currency);
                sm.setC_quantity(s_amount.abs());
                sm.setC_amount(s_native_Amount_USD);
                sm.setC_trans_code(TransactionKindEnum.Other);

                log.error("need to add actions: " + toString());
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
        final StringBuffer sb = new StringBuffer("CryptoAppAccountStatementTransaction{");
        sb.append("s_timestamp=").append(s_timestamp);
        sb.append(", s_transaction_description='").append(s_transaction_description).append('\'');
        sb.append(", s_crypto_currency='").append(s_crypto_currency).append('\'');
        sb.append(", s_amount=").append(s_amount);
        sb.append(", s_to_Currency='").append(s_to_Currency).append('\'');
        sb.append(", s_to_Amount=").append(s_to_Amount);
        sb.append(", s_native_Currency='").append(s_native_Currency).append('\'');
        sb.append(", s_native_Amount=").append(s_native_Amount);
        sb.append(", s_native_Amount_USD=").append(s_native_Amount_USD);
        sb.append(", s_transaction_kind='").append(s_transaction_kind).append('\'');
        sb.append(", s_transaction_hash='").append(s_transaction_hash).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
