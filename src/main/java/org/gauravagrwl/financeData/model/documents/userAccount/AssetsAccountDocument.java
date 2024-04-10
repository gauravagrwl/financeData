package org.gauravagrwl.financeData.model.documents.userAccount;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("AssetsAccountDocument")
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
    public void calculate(BigDecimal amount) {

    }


//    @Override
//    public MappingStrategy<? extends AccountStatementDocument> getMappingStrategy(String profileType) {
//        MappingStrategy<BankAccountStatementDocument> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<BankAccountStatementDocument>()
//                .withForceCorrectRecordLength(true).build();
//        headerColumnNameMappingStrategy.setProfile(profileType);
//        headerColumnNameMappingStrategy.setType(BankAccountStatementDocument.class);
//        return headerColumnNameMappingStrategy;
//    }

}