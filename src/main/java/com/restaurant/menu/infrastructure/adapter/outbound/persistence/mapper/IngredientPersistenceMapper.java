package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.Ingredient;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.IngredientJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UUIDMapper.class)
public interface IngredientPersistenceMapper {

    IngredientJpaEntity toJpaEntity(Ingredient domain);

    Ingredient toDomain(IngredientJpaEntity jpaEntity);
}
