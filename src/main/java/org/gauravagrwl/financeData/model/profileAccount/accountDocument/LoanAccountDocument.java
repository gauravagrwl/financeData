package org.gauravagrwl.financeData.model.profileAccount.accountDocument;

import java.math.BigDecimal;

import lombok.*;
import org.springframework.data.mongodb.core.query.Update;

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
