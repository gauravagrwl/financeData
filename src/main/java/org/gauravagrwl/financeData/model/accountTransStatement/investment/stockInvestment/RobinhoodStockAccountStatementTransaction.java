package org.gauravagrwl.financeData.model.accountTransStatement.investment.stockInvestment;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.CsvMDYDateStringToDateConverter;
import org.gauravagrwl.financeData.helper.TransactionKindEnum;
import org.gauravagrwl.financeData.model.accountTransStatement.AccountStatementTransaction;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.statementModel.StatementModel;
import org.gauravagrwl.financeData.model.statementModel.StockInvestmentAccountStatementModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class RobinhoodStockAccountStatementTransaction extends AccountStatementTransaction {

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Activity Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate s_activity_Date;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Process Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate s_process_Date;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Settle Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate s_settle_Date;

    @CsvBindByNames({
            @CsvBindByName(column = "Instrument", profiles = {
                    "Robinhood_STOCK"}),})
    private String s_instrument;

    @CsvBindByNames({
            @CsvBindByName(column = "Description", profiles = {
                    "Robinhood_STOCK"}),})
    private String s_description;

    @CsvBindByNames({
            @CsvBindByName(column = "Trans Code", profiles = {
                    "Robinhood_STOCK"}),})
    private String s_trans_Code;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Quantity", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal s_quantity;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Price", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal s_price;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal s_amount;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RobinhoodStockAccountStatementTransaction{");
        sb.append("s_activity_Date=").append(s_activity_Date);
        sb.append(", s_process_Date=").append(s_process_Date);
        sb.append(", s_settle_Date=").append(s_settle_Date);
        sb.append(", s_instrument='").append(s_instrument).append('\'');
        sb.append(", s_description='").append(s_description).append('\'');
        sb.append(", s_trans_Code='").append(s_trans_Code).append('\'');
        sb.append(", s_quantity=").append(s_quantity);
        sb.append(", s_price=").append(s_price);
        sb.append(", s_amount=").append(s_amount);
        sb.append('}');
        return sb.toString();
    }

    @Override
    /**
     * Buy: instrument(assets / coin) added under any transaction code.
     * Sell: instrument(assets / coin) removed or sold under any transaction code.
     * O_*: Option transactions
     * Transfer: instrument(assets / coin) is transfer
     * Earn: Div, REC, SLIP, MISC
     * Charge:DTAX,
     */
    public List<StatementModel> updateAccountStatement(AccountCollection accountCollection) {
        List<StatementModel> statementModelList = new ArrayList<>();
        StockInvestmentAccountStatementModel statementModel = new StockInvestmentAccountStatementModel();
        setAccountDocumentId(accountCollection.getId());
        statementModel.setAccountId(accountCollection.getId());
        statementModel.setAccountStatementId(getId());
        statementModel.setC_instrument(s_instrument);
        statementModel.setC_transaction_dateTime(ZonedDateTime.of(s_settle_Date, LocalTime.NOON, ZoneId.of("UTC")));
        statementModel.setC_description(s_description);
        statementModel.setC_quantity(s_quantity);

        statementModel.setDuplicate(getDuplicate());

        if (s_price.compareTo(BigDecimal.ZERO) != 0) {
            if (StringUtils.equalsAnyIgnoreCase(s_trans_Code, "STO", "BTC")) {
                statementModel.setC_fee((s_amount.abs().subtract(s_quantity.multiply(s_price).multiply(BigDecimal.valueOf(100)))).abs().setScale(2, RoundingMode.UP));
            } else {
                statementModel.setC_fee((s_amount.abs().subtract(s_quantity.multiply(s_price))).abs().setScale(2, RoundingMode.UP));
            }
        }
        switch (s_trans_Code) {
            case "OEXP" -> {
                statementModel.setC_trans_code(TransactionKindEnum.O_EXP);

            }
            case "Buy" -> {
                statementModel.setC_trans_code(TransactionKindEnum.Buy);
            }
            case "STO" -> {
                statementModel.setC_trans_code(TransactionKindEnum.O_STO);

            }
            case "OASGN" -> {
                statementModel.setC_trans_code(TransactionKindEnum.O_ASGN);

            }
            case "Sell" -> {
                statementModel.setC_trans_code(TransactionKindEnum.Sell);
            }
            case "BTC" -> {
                statementModel.setC_trans_code(TransactionKindEnum.O_BTC);
            }
            case "ACH" -> {
                if (StringUtils.containsIgnoreCase(s_description, "Deposit")) {
                    statementModel.setC_trans_code(TransactionKindEnum.Deposit);
                } else if (StringUtils.containsIgnoreCase(s_description, "Withdrawal")) {
                    statementModel.setC_trans_code(TransactionKindEnum.Withdrawal);
                } else if (StringUtils.containsIgnoreCase(s_description, "Cancel")) {
                    statementModel.setC_trans_code(TransactionKindEnum.Cancel);
                }
            }
            case "DTAX" -> {
                statementModel.setC_trans_code(TransactionKindEnum.Charge);
            }
            case "CDIV", "OCA", "SLIP", "REC" -> {
                statementModel.setC_trans_code(TransactionKindEnum.Earn);
            }
            case "MISC" -> {
                if (BigDecimal.ZERO.compareTo(s_price) > 0) {
                    statementModel.setC_trans_code(TransactionKindEnum.Earn);
                } else {
                    statementModel.setC_trans_code(TransactionKindEnum.Charge);
                }
            }
            default -> {
                log.error("Transaction Code not define: " + toString());
                throw new FinanceDataException("Process fail");
            }
        }
        statementModel.setC_price(s_price.abs().setScale(2, RoundingMode.UP));
        statementModel.setC_amount(s_amount.abs().setScale(2, RoundingMode.UP));

        statementModelList.add(statementModel);
        return statementModelList;
    }
}
