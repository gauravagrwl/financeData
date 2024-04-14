package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LoanAccountDocument extends AccountDocument {

    private BigDecimal loanAmount;
    private BigDecimal remaingAmount;
    private BigDecimal amountPaid;
    private String rateOfIntrest;
    private String purpose;
    private String remaingInstallment;

    @Override
    public MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(String mappingProfile) {
        return null;
    }

    @Override
    public Update getUpdateBalanceUpdateQuery(BigDecimal amount) {
        return Update.update("loanAmount", amount);

    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return getLoanAmount();
    }

    @Override
    public BigDecimal calculateAccountBalance() {
        return null;
    }
}
