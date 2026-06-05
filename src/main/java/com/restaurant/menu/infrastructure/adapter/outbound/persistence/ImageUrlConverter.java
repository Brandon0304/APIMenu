package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.ImageUrl;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class ImageUrlConverter implements AttributeConverter<ImageUrl, String> {

    @Override
    public String convertToDatabaseColumn(ImageUrl imageUrl) {
        return imageUrl != null ? imageUrl.url() : null;
    }

    @Override
    public ImageUrl convertToEntityAttribute(String value) {
        return value != null ? new ImageUrl(value) : null;
    }
}
