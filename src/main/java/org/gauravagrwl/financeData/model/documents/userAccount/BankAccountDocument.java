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
@JsonTypeName("BankAccountDocument")
public class BankAccountDocument extends AccountDocument {

    // Account Calculated Balance
    private BigDecimal accountBalance = BigDecimal.ZERO;

    // Account holding type
    private String holdingType;

    // Account Code: Routing code or IIFC code.
    private String accountCode;

    // Code Type: Routing or IIFC
    private String accountCodeType;

    @Override
    public void calculate(BigDecimal amount) {
        this.accountBalance = amount;
    }

//    @Override
//    public MappingStrategy<? extends AccountStatementDocument> getMappingStrategy(String profileType) {
//        MappingStrategy<BankAccountStatementDocument> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<BankAccountStatementDocument>()
//                .withForceCorrectRecordLength(true).build();
//        headerColumnNameMappingStrategy.setProfile(profileType);
//        headerColumnNameMappingStrategy.setType(BankAccountStatementDocument.class);
//        return headerColumnNameMappingStrategy;
//    }

//    @Override
//    public MappingStrategy<BankAccountStatementDocument> getMappingStrategy(String profileType) {
//        MappingStrategy<BankAccountStatementDocument> headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategyBuilder<BankAccountStatementDocument>()
//                .withForceCorrectRecordLength(true).build();
//        headerColumnNameMappingStrategy.setProfile(profileType);
//        headerColumnNameMappingStrategy.setType(BankAccountStatementDocument.class);
//        return headerColumnNameMappingStrategy;
//    }


}