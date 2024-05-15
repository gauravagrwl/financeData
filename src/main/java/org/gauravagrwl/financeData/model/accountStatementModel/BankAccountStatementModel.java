package org.gauravagrwl.financeData.model.accountStatementModel;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BankAccountStatementModel extends StatementModel {


    private LocalDate c_transactionDate;

    private String c_description;

    private BigDecimal c_debit = BigDecimal.ZERO; // Amount

    private BigDecimal c_credit = BigDecimal.ZERO; // Amount

    private String c_type;

    private String c_notes;

    private BigDecimal c_transactionBalance;

    private AccountStatementTransaction statement;

}
