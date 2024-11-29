package org.gauravagrwl.financeData.model.userAccounts.reports;

import lombok.Getter;
import lombok.Setter;
import org.gauravagrwl.financeData.helper.FinanceAppHelper;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@Document(collection = "HoldingReportStatement")
public class HoldingReportStatement extends ReportStatement {
    private String instrument;
    private BigDecimal quantity = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    private BigDecimal stakeQuantity = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    private BigDecimal avgBuyPrice = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    private BigDecimal profitLoss = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    private BigDecimal amount = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    private BigDecimal optionEarning = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    private BigDecimal stakeEarning = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);
    private BigDecimal quantityEarning = BigDecimal.ZERO.setScale(FinanceAppHelper.currencyScale, RoundingMode.UP);

    @Override
    public String toString() {
        return "HoldingReportStatement{" +
                "instrument='" + instrument + '\'' +
                ", quantity=" + quantity +
                ", avgBuyPrice=" + avgBuyPrice +
                ", profitLoss=" + profitLoss +
                ", amount=" + amount +
                ", optionEarning=" + optionEarning +
                '}';
    }
}
