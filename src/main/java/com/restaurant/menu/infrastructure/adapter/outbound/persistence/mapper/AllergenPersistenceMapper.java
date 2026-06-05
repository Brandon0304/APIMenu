package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.Allergen;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.AllergenJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UUIDMapper.class)
public interface AllergenPersistenceMapper {

    AllergenJpaEntity toJpaEntity(Allergen domain);

    Allergen toDomain(AllergenJpaEntity jpaEntity);
}
