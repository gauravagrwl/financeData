package org.gauravagrwl.financeData.helper;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Slf4j
public class CsvAmountStringToBigDecimalConverter extends AbstractBeanField<String, BigDecimal> {

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        Boolean isNegative = false;
        if (value.contains("(") || value.contains(")")) {
            log.info("Negative number: " + value);
            isNegative = true;
        }
        value = value.replaceAll("[^-1234567890.]", "");
        if (StringUtils.isEmpty(value) || StringUtils.equals("-", value)) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal bigDecimal = new BigDecimal(value);
            return (isNegative) ? (BigDecimal.ZERO.subtract(bigDecimal)) : bigDecimal;
        }
    }
}

//TODO: Handle negative number like (240) which is -240
