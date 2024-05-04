package org.gauravagrwl.financeData.model.statementModel;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.TransactionKindEnum;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class StockInvestmentAccountStatementModel extends InvestmentStatementModel {


    private String c_instrument;

    private ZonedDateTime c_transaction_dateTime;

    private String c_description;

    private TransactionKindEnum c_trans_code;

    private BigDecimal c_quantity;

    private BigDecimal c_price;

    private BigDecimal c_amount;

    private BigDecimal c_fee;


    public String findByKeyAssets() {
        return c_instrument;
    }

    @Override
    public String toString() {
        return "StockInvestmentAccountStatementModel{" +
                "c_instrument='" + c_instrument + '\'' +
                ", c_settle_Date=" + c_transaction_dateTime +
                ", c_description='" + c_description + '\'' +
                ", c_trans_code=" + c_trans_code +
                ", c_quantity=" + c_quantity +
                ", c_price=" + c_price +
                ", c_amount=" + c_amount +
                ", c_fee=" + c_fee +
                '}';
    }
}
