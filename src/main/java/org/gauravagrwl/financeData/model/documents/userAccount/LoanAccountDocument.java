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
@JsonTypeName("LoanAccountDocument")
public class LoanAccountDocument extends AccountDocument {

    private BigDecimal loanAmount;
    private BigDecimal remaingAmount;
    private BigDecimal amountPaid;
    private String rateOfIntrest;
    private String purpose;
    private String remaingInstallment;

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