package org.gauravagrwl.financeData.helper.converters;

import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

public class BigDecimalWriteConverter implements Converter<BigDecimal, Double> {

    @Override
    public Double convert(BigDecimal source) {
        return source.setScale(2).doubleValue();
    }
}
