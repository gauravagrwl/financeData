package org.gauravagrwl.financeData.config;

import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

public class BigDecimalReadConverter implements Converter<Double, BigDecimal> {
    @Override
    public BigDecimal convert(Double source) {
        return BigDecimal.valueOf(source);
    }
}
