package org.gauravagrwl.financeData.helper.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CsvMDYDateStringToDateConverter extends AbstractBeanField<String, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        LocalDate parse = LocalDate.parse(value, formatter);
        return parse;

    }
}
