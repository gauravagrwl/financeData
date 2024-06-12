package org.gauravagrwl.financeData.helper.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

public class CsvDateTimeToUTCDateTimeConverter extends AbstractBeanField<String, LocalDateTime> {
    private static final DateTimeFormatter LOCAL_DATE_TIME_COINBASE_FORMATER;

    private static final DateTimeFormatter LOCAL_DATE_TIME_CRYPTOAPP_FORMATER;

    static {
        LOCAL_DATE_TIME_COINBASE_FORMATER = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(ISO_LOCAL_DATE)
                .appendLiteral(' ')
                .append(ISO_LOCAL_TIME)
                .appendLiteral(' ')
                .parseCaseSensitive()
                .appendZoneRegionId()
                .toFormatter();

        LOCAL_DATE_TIME_CRYPTOAPP_FORMATER = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(ISO_LOCAL_DATE)
                .appendLiteral(' ')
                .append(ISO_LOCAL_TIME)
                .toFormatter();
    }

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (StringUtils.containsIgnoreCase(value, "UTC")) {
            LocalDateTime ldt = LocalDateTime.parse(value, LOCAL_DATE_TIME_COINBASE_FORMATER);
            ZonedDateTime zdt = ZonedDateTime.parse(value, LOCAL_DATE_TIME_COINBASE_FORMATER);
            return zdt;
        } else {
            LocalDateTime ldt = LocalDateTime.parse(value, LOCAL_DATE_TIME_CRYPTOAPP_FORMATER);
            ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));
            return zdt;
        }
    }
}