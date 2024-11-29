package org.gauravagrwl.financeData.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentBalanceCalculatedRecords {
    String id;
    BigDecimal total;

    @Override
    public String toString() {
        return "InvestmentBalanceCalculatedRecords{" +
                "transactionType='" + id + '\'' +
                ", total=" + total +
                '}';
    }
}
