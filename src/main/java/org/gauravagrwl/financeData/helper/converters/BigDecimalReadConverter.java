package org.gauravagrwl.financeData.helper.converters;

import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

public class BigDecimalReadConverter implements Converter<Double, BigDecimal> {


    @Override
    public BigDecimal convert(Double source) {
        return new BigDecimal(source).setScale(2);
    }
}
