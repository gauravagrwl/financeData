package org.gauravagrwl.financeData.helper.converters;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

public class BigDecimalWriteConverter implements Converter<BigDecimal, Decimal128> {

    @Override
    public Decimal128 convert(BigDecimal source) {
        return new Decimal128(source);
    }
}
