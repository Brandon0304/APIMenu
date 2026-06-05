package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.PreparationTime;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class PreparationTimeConverter implements AttributeConverter<PreparationTime, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PreparationTime preparationTime) {
        return preparationTime != null ? preparationTime.minutes() : null;
    }

    @Override
    public PreparationTime convertToEntityAttribute(Integer value) {
        return value != null ? new PreparationTime(value) : null;
    }
}
