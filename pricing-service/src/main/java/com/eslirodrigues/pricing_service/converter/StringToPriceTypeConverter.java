package com.eslirodrigues.pricing_service.converter;

import com.eslirodrigues.pricing_service.entity.PriceType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPriceTypeConverter implements Converter<String, PriceType> {
    @Override
    public PriceType convert(String source) {
        return PriceType.fromValue(source);
    }
}