package org.gauravagrwl.financeData.model.profileAccount.accountStatement;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByNames;
import org.gauravagrwl.financeData.helper.CsvAmountStringToBigDecimalConverter;
import org.gauravagrwl.financeData.helper.CsvMDYDateStringToDateConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestmentStockAccountStatement extends AccountStatementDocument {

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Activity Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate activityDate;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Process Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate processDate;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Settle Date", converter = CsvMDYDateStringToDateConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private LocalDate settleDate;

    @CsvBindByNames({
            @CsvBindByName(column = "Instrument", profiles = {
                    "Robinhood_STOCK"}),})
    private String instrument;

    @CsvBindByNames({
            @CsvBindByName(column = "Description", profiles = {
                    "Robinhood_STOCK"}),})
    private String description;

    @CsvBindByNames({
            @CsvBindByName(column = "Trans Code", profiles = {
                    "Robinhood_STOCK"}),})
    private String transCode;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Quantity", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal quantity;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Price", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal rate;

    @CsvCustomBindByNames({
            @CsvCustomBindByName(column = "Amount", converter = CsvAmountStringToBigDecimalConverter.class, profiles = {
                    "Robinhood_STOCK"}),})
    private BigDecimal amount;

    private BigDecimal fee;


}
