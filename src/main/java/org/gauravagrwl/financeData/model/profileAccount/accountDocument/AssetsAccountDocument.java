package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import com.opencsv.bean.MappingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.profileAccount.accountStatement.AccountStatementDocument;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssetsAccountDocument extends AccountDocument {

    // Total amount for the purchase.
    private BigDecimal amountInvestment;

    // Asset name
    private String propertyName;

    // Asset acquired Year
    private String acquiredYear;

    private boolean isRentable;

    private String tenantName;

    private String tenantNumber;


    @Override
    public List<? extends AccountStatementDocument> calculateAccountBalance(List<? extends AccountStatementDocument> statementDocumentList) {
        return null;
    }

    @Override
    public MappingStrategy<? extends AccountStatementDocument> getHeaderColumnNameMappingStrategy(String mappingProfile) {
        return null;
    }

    @Override
    public Update getUpdateBalanceUpdateQuery(BigDecimal amount) {
        return Update.update("amountInvestment", amount);

    }

    @Override
    public Query findDuplicateRecordQuery(AccountStatementDocument statementDocument) {
        return null;
    }

    @Override
    public BigDecimal getAccountStatementBalance() {
        return null;
    }
}
