package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.ImageUrl;
import com.restaurant.menu.domain.model.NutritionalInfo;
import com.restaurant.menu.domain.model.PreparationTime;
import com.restaurant.menu.domain.model.Price;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.NutritionalInfoJpaEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ValueObjectMapper {

    default BigDecimal mapPriceToBigDecimal(Price price) {
        return price != null ? price.value() : null;
    }

    default Price mapBigDecimalToPrice(BigDecimal value) {
        return value != null ? new Price(value) : null;
    }

    default Integer mapPreparationTimeToInt(PreparationTime time) {
        return time != null ? time.minutes() : null;
    }

    default PreparationTime mapIntToPreparationTime(Integer value) {
        return value != null ? new PreparationTime(value) : null;
    }

    default String mapImageUrlToString(ImageUrl url) {
        return url != null ? url.url() : null;
    }

    default ImageUrl mapStringToImageUrl(String value) {
        return value != null ? new ImageUrl(value) : null;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "menuItemId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "calories", target = "calories")
    @Mapping(source = "proteinGrams", target = "proteinGrams")
    @Mapping(source = "carbsGrams", target = "carbsGrams")
    @Mapping(source = "fatGrams", target = "fatGrams")
    @Mapping(source = "fiberGrams", target = "fiberGrams")
    @Mapping(source = "sodiumMg", target = "sodiumMg")
    NutritionalInfoJpaEntity toJpaEntity(NutritionalInfo domain);

    NutritionalInfo toDomain(NutritionalInfoJpaEntity jpaEntity);
}
