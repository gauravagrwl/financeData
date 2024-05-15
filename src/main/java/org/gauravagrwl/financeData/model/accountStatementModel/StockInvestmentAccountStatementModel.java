package org.gauravagrwl.financeData.model.accountStatementModel;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.enums.TransactionKindEnum;

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

    private BigDecimal c_stake_quantity;

    private BigDecimal c_price;

    private BigDecimal c_amount;

    private BigDecimal c_fee;


    public String findByKeyAssets() {
        return c_instrument;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StockInvestmentAccountStatementModel{");
        sb.append("c_instrument='").append(c_instrument).append('\'');
        sb.append(", c_transaction_dateTime=").append(c_transaction_dateTime);
        sb.append(", c_description='").append(c_description).append('\'');
        sb.append(", c_trans_code=").append(c_trans_code);
        sb.append(", c_quantity=").append(c_quantity);
        sb.append(", c_stake_quantity=").append(c_stake_quantity);
        sb.append(", c_price=").append(c_price);
        sb.append(", c_amount=").append(c_amount);
        sb.append(", c_fee=").append(c_fee);
        sb.append('}');
        return sb.toString();
    }
}
