package org.gauravagrwl.financeData.model.userAccounts.reports;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Document(collection = "HoldingReportStatement")
public class HoldingReportStatement extends ReportStatement {
    List<String> accountStatementId;
    private String instrument;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal profitLoss;
    private BigDecimal amount;
}
