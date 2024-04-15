package org.gauravagrwl.financeData.model.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CashFlowReportDocument extends ReportDocument {

    private LocalDate transactionDate; // Date Of Transactions
    private int year;
    private String description;
    private String transactionType; // CashIn or CashOut
    private BigDecimal cashIn;
    private BigDecimal cashOut;
    private Boolean reconciled;
    private AuditMetadata audit = new AuditMetadata();
    @Version
    private Integer version;

}
