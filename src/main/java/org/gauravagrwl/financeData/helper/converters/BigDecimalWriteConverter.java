package org.gauravagrwl.financeData.helper.converters;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalWriteConverter implements Converter<BigDecimal, Decimal128> {

    @Override
    public Decimal128 convert(BigDecimal source) {
        if (source.scale() > 10)
                source = source.setScale(10, RoundingMode.UP);
        return new Decimal128(source);
    }
}
