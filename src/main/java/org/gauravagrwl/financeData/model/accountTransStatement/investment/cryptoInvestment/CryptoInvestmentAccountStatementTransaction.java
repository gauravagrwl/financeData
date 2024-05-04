package org.gauravagrwl.financeData.model.accountTransStatement.investment.cryptoInvestment;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.TransactionKindEnum;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class CryptoInvestmentAccountStatementTransaction extends AccountStatementTransaction {

    //Date of the transactions
    private LocalDateTime c_transactionDate;
    // Coin in transactions
    // Crypto App Currency
    private String c_coin;

    // Decriptions of Transactions
    private String c_decriptions;

    // Total Quantity
    // Crypto App - Amount
    private BigDecimal c_quantity;

    // Purchase price
    //Crypto App -- Need to calculate
    private BigDecimal c_rate;

    // Total Amount:
    // Crypto App -- Native Amount (in USD)
    private BigDecimal c_total_amount;

    // Transaction Code
    private TransactionKindEnum trans_code;

    private BigDecimal c_fee;


}
