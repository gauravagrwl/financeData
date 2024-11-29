package org.gauravagrwl.financeData.model.userAccounts.transactions.investmentTransactions;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.helper.FinanceAppHelper;
import org.gauravagrwl.financeData.helper.converters.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.converters.CsvDateTimeToUTCDateTimeConverter;
import org.gauravagrwl.financeData.helper.enums.TransactionType;
import org.gauravagrwl.financeData.model.userAccounts.statements.AccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.statements.InvestmentAccountStatement;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class CryptoAppAccountTransaction extends AccountTransaction {

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Timestamp (UTC)", converter = CsvDateTimeToUTCDateTimeConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private ZonedDateTime timeStamp;

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Description", profiles = {
                    "CryptoApp_CRYPTO"}),})
    private String transactionDescription;

    @CsvBindByNames({
            @CsvBindByName(column = "Currency", profiles = {
                    "CryptoApp_CRYPTO"}),})
    private String cryptoCoin;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal cryptoAmount;

    @CsvBindByNames({
            @CsvBindByName(column = "To Currency", profiles = {
                    "CryptoApp_CRYPTO"}),})
    private String toCryptoCoin;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "To Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal toCryptoAmount;

    @CsvBindByNames({
            @CsvBindByName(column = "Native Currency", profiles = {
                    "CryptoApp_CRYPTO"}),})
    private String nativeCurrency;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Native Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal nativeAmount;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Native Amount (in USD)", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "CryptoApp_CRYPTO"}),})
    private BigDecimal nativeAmountInUsd;

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Kind", profiles = {
                    "CryptoApp_CRYPTO"}),})
    private String transactionKind;

    @CsvBindByNames({
            @CsvBindByName(column = "Transaction Hash", profiles = {
                    "CryptoApp_CRYPTO"}),})
    private String transactionhash;

    @Getter
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'accountTransactionIds':?#{#self._id} }", collection = "InvestmentAccountStatement")
    List<AccountStatement> accountStatements;


    @Override
    public AccountStatement transformToStatement() {
        return null;
    }


    @Override
    public List<AccountStatement> transformToStatementList() {
        InvestmentAccountStatement statement = new InvestmentAccountStatement();
        List<AccountStatement> accountStatementList = new ArrayList<>();
        setStatementInitialValue(statement);

        //If USD deposit
        if (cryptoCoin.equalsIgnoreCase("USD")) {
            statement.setTransactionType(determineTransactionType(transactionKind));
        } else {
            statement.setInstrument(cryptoCoin);
            statement.setQuantity(cryptoAmount.abs().setScale(FinanceAppHelper.cryptoScale, RoundingMode.UP));
            statement.setTransactionType(determineTransactionType(transactionKind));
            statement.setRate(nativeAmountInUsd.divide(cryptoAmount, FinanceAppHelper.currencyScale, RoundingMode.UP));
        }
        if (!toCryptoCoin.isEmpty()) {
            accountStatementList.add(getSecondAccountStatement());
        }
        accountStatementList.add(statement);
        return accountStatementList;
    }


    public AccountStatement getSecondAccountStatement() {
        InvestmentAccountStatement statement = new InvestmentAccountStatement();
        setStatementInitialValue(statement);
        statement.setTransactionDate(statement.getTransactionDate().plusSeconds(1L));
        statement.setInstrument(toCryptoCoin);
        statement.setQuantity(toCryptoAmount.abs().setScale(FinanceAppHelper.cryptoScale, RoundingMode.UP));
        statement.setTransactionType(TransactionType.BUY);
        statement.setRate(nativeAmountInUsd.divide(toCryptoAmount, FinanceAppHelper.currencyScale, RoundingMode.UP));
        return statement;
    }

    private void setStatementInitialValue(InvestmentAccountStatement statement) {
        statement.getAccountTransactionIds().add(getId());
        statement.setAccountId(getUserAccountId());
        statement.setCurrency(getCurrency());
        statement.setTransactionDate(timeStamp);
        statement.setDescription(transactionDescription);
        statement.setAmount(nativeAmountInUsd.abs().setScale(FinanceAppHelper.currencyScale, RoundingMode.UP));
    }

    private TransactionType determineTransactionType(String transCode) {
        TransactionType type = null;
        switch (transCode) {
            case "admin_wallet_credited", "crypto_wallet_swap_credited", "dust_conversion_credited", "reimbursement" ->
                    type = TransactionType.BUY;
            case "card_cashback_reverted", "reimbursement_reverted" -> type = TransactionType.EARN_REVERT;
            case "crypto_earn_extra_interest_paid", "crypto_earn_interest_paid", "mco_stake_reward",
                 "referral_card_cashback", "referral_gift", "rewards_platform_deposit_credited" ->
                    type = TransactionType.EARN;
            case "crypto_earn_program_created", "lockup_lock" -> type = TransactionType.STAKE_DEPOSIT;
            case "crypto_earn_program_withdrawn" -> type = TransactionType.STAKE_WITHDRAWAL;
            case "crypto_exchange" -> type = TransactionType.SELL; // Statement 2 will be buy
            case "crypto_wallet_swap_debited" -> type = TransactionType.SELL;
            case "dust_conversion_debited" -> type = TransactionType.SELL;
            case "viban_deposit_precredit", "viban_purchase" -> type = TransactionType.DEPOSIT;
            case "viban_deposit_precredit_repayment" -> type = TransactionType.WITHDRAWAL;


            default -> throw new FinanceAppException("No Switch statement defined for : %s", transCode);

//            case "admin_wallet_credited" -> type = TransactionType.BUY;
//            case "card_cashback_reverted" -> type = TransactionType.EARN_REVERT;
//
//            case "crypto_earn_extra_interest_paid" -> type = TransactionType.EARN;
//            case "crypto_earn_interest_paid" -> type = TransactionType.EARN;
//
//            case "crypto_earn_program_created" -> type = TransactionType.STAKE_DEPOSIT;
//            case "crypto_earn_program_withdrawn" -> type = TransactionType.STAKE_WITHDRAWAL;
//            case "crypto_exchange" -> type = TransactionType.SELL; // Statement 2 will be buy
//            case "crypto_wallet_swap_credited" -> type = TransactionType.BUY;
//            case "crypto_wallet_swap_debited" -> type = TransactionType.SELL;
//
//            case "dust_conversion_credited" -> type = TransactionType.BUY;
//            case "dust_conversion_debited" -> type = TransactionType.SELL;
//
//            case "lockup_lock" -> type = TransactionType.STAKE_DEPOSIT;
//
//            case "mco_stake_reward" -> type = TransactionType.EARN;
//            case "referral_card_cashback" -> type = TransactionType.EARN;
//            case "referral_gift" -> type = TransactionType.EARN;
//            case "reimbursement" -> type = TransactionType.BUY;
//            case "reimbursement_reverted" -> type = TransactionType.EARN_REVERT;
//            case "rewards_platform_deposit_credited" -> type = TransactionType.EARN;
//
//            case "viban_deposit_precredit" -> type = TransactionType.DEPOSIT;
//            case "viban_deposit_precredit_repayment" -> type = TransactionType.WITHDRAWAL;
//            case "viban_purchase" -> type = TransactionType.DEPOSIT;
        }
        return type;
    }
}
