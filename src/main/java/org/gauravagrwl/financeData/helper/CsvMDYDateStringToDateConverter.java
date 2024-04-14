package org.gauravagrwl.financeData.helper;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CsvMDYDateStringToDateConverter extends AbstractBeanField<String, LocalDate> {
    // private static final DateTimeFormatter formatter =
    // DateTimeFormatter.ofPattern("MM/dd/yyyy");
    // private static final DateTimeFormatter formatter =
    // DateTimeFormatter.ofPattern("d-MMM-yyyy");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        log.info("Parsing : " + value);
        LocalDate parse = LocalDate.parse(value, formatter);
        return parse;

    }

}
