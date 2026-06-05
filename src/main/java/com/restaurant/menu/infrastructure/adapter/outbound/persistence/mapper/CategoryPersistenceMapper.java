package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.Category;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.CategoryJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UUIDMapper.class)
public interface CategoryPersistenceMapper {

    CategoryJpaEntity toJpaEntity(Category domain);

    Category toDomain(CategoryJpaEntity jpaEntity);
}
