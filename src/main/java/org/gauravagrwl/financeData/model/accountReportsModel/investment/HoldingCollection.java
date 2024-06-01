package org.gauravagrwl.financeData.model.accountReportsModel.investment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.accountReportsModel.ReportCollection;
import org.gauravagrwl.financeData.model.accountStatementModel.StatementModel;
import org.gauravagrwl.financeData.model.accountStatementModel.StockInvestmentAccountStatementModel;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class HoldingCollection extends ReportCollection implements HoldingOperations {

    @NotBlank
    @Indexed(unique = true, background = true)
    private String instrument;

    private BigDecimal totalQuantity = BigDecimal.ZERO;

    private BigDecimal averageRate = BigDecimal.ZERO;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private BigDecimal optionAmount = BigDecimal.ZERO;

    private BigDecimal divAmount = BigDecimal.ZERO;

    private BigDecimal profitLoss = BigDecimal.ZERO;

    private Boolean need_correction = Boolean.FALSE;

    private BigDecimal totalQuantityStake = BigDecimal.ZERO;

    List<AccountStatementTransaction> holdingTransactionList = new ArrayList<>();


    private void resetFields() {
        totalQuantity = BigDecimal.ZERO;
        averageRate = BigDecimal.ZERO;
        totalAmount = BigDecimal.ZERO;
        optionAmount = BigDecimal.ZERO;
        divAmount = BigDecimal.ZERO;
        profitLoss = BigDecimal.ZERO;
        need_correction = Boolean.FALSE;
    }


    @Override
    public void calculateHolding(StatementModel accountStatement) {
        StockInvestmentAccountStatementModel s = (StockInvestmentAccountStatementModel) accountStatement;
        log.info(s.toString());
        switch (s.getC_trans_code()) {
            case Buy -> {
                totalQuantity = totalQuantity.add(s.getC_quantity());
                totalAmount = totalAmount.add(s.getC_amount().abs());
                averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
            }
            case Sell -> {
                if (totalQuantity.compareTo(s.getC_quantity()) == 0) {
                    // Complete Sold-out
                    profitLoss = profitLoss.add(s.getC_amount().subtract(totalAmount)).setScale(2, RoundingMode.UP);
                    averageRate = totalQuantity = totalAmount = BigDecimal.ZERO;
                } else if (totalQuantity.compareTo(s.getC_quantity()) == 1) {
                    // calculate profilt on that partial sold.
                    profitLoss = profitLoss.add(s.getC_amount().subtract(s.getC_quantity().multiply(averageRate))).setScale(2, RoundingMode.UP);
                    // Partial sold
                    totalQuantity = totalQuantity.subtract(s.getC_quantity());
                    totalAmount = totalQuantity.multiply(averageRate);
                } else {
                    need_correction = Boolean.TRUE;
                }
            }
            case Earn -> {
                totalAmount = totalAmount.add(s.getC_amount().abs());
                totalQuantity = totalQuantity.add(s.getC_quantity());
                if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                    averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
                }
            }
            case Charge -> {
                totalAmount = totalAmount.subtract(s.getC_amount().abs());
                if (BigDecimal.ZERO.compareTo(totalQuantity) != 0) {
                    averageRate = totalAmount.divide(totalQuantity, RoundingMode.HALF_UP);
                }
            }
            case O_STO -> {
                optionAmount = optionAmount.add(s.getC_amount());
            }
            case O_BTC -> {
                optionAmount = optionAmount.subtract(s.getC_amount().abs());
            }
            case Stake -> {
                totalQuantityStake = totalQuantityStake.add(s.getC_stake_quantity());
            }
            case StakeWithdrawl -> {
                totalQuantityStake = totalQuantityStake.subtract(s.getC_stake_quantity());
                if (totalQuantityStake.compareTo(BigDecimal.ZERO) == 0) {
                    totalQuantityStake = BigDecimal.ZERO.setScale(2);
                }
            }
            default -> {
                log.info("No rule define for statement: " + s);
            }

        }
        log.info(this.toString());
    }

    @Override
    public List<AccountStatementTransaction> getHoldingTransactionList() {
        return holdingTransactionList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HoldingCollection{");
        sb.append("instrument='").append(instrument).append('\'');
        sb.append(", totalQuantity=").append(totalQuantity);
        sb.append(", averageRate=").append(averageRate);
        sb.append(", totalAmount=").append(totalAmount);
        sb.append(", optionAmount=").append(optionAmount);
        sb.append(", divAmount=").append(divAmount);
        sb.append(", profitLoss=").append(profitLoss);
        sb.append(", need_correction=").append(need_correction);
        sb.append(", totalQuantityStake=").append(totalQuantityStake);
//        sb.append(", holdingTransactionList=").append(holdingTransactionList);
        sb.append('}');
        return sb.toString();
    }


}
