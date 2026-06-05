package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.Price;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class PriceConverter implements AttributeConverter<Price, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Price price) {
        return price != null ? price.value() : null;
    }

    @Override
    public Price convertToEntityAttribute(BigDecimal value) {
        return value != null ? new Price(value) : null;
    }
}
