package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.ModifierGroup;
import com.restaurant.menu.domain.model.ModifierOption;
import com.restaurant.menu.domain.model.ModifierOptionId;
import com.restaurant.menu.domain.model.Price;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.ModifierGroupJpaEntity;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.ModifierOptionJpaEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UUIDMapper.class)
public interface ModifierGroupPersistenceMapper {

    @Mapping(target = "options", source = "options")
    ModifierGroupJpaEntity toJpaEntity(ModifierGroup domain);

    ModifierGroup toDomain(ModifierGroupJpaEntity jpaEntity);

    default ModifierOptionJpaEntity map(ModifierOption option) {
        if (option == null) return null;
        ModifierOptionJpaEntity entity = new ModifierOptionJpaEntity();
        entity.setId(option.getId() != null ? option.getId().value() : null);
        entity.setName(option.getName());
        entity.setPriceAdjustment(option.getPriceAdjustment() != null ? option.getPriceAdjustment().value() : BigDecimal.ZERO);
        entity.setActive(option.isActive());
        return entity;
    }

    default ModifierOption map(ModifierOptionJpaEntity entity) {
        if (entity == null) return null;
        ModifierOptionId id = entity.getId() != null ? new ModifierOptionId(entity.getId()) : null;
        Price price = entity.getPriceAdjustment() != null ? new Price(entity.getPriceAdjustment()) : Price.zero();
        return new ModifierOption(id, entity.getName(), price, entity.isActive());
    }

    default ArrayList<ModifierOptionJpaEntity> mapOptions(java.util.List<ModifierOption> options) {
        if (options == null) return null;
        ArrayList<ModifierOptionJpaEntity> result = new ArrayList<>();
        for (ModifierOption opt : options) {
            result.add(map(opt));
        }
        return result;
    }

    default ArrayList<ModifierOption> mapOptionsJpa(java.util.List<ModifierOptionJpaEntity> entities) {
        if (entities == null) return null;
        ArrayList<ModifierOption> result = new ArrayList<>();
        for (ModifierOptionJpaEntity entity : entities) {
            result.add(map(entity));
        }
        return result;
    }
}
