package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.Menu;
import com.restaurant.menu.domain.model.MenuSection;
import com.restaurant.menu.domain.model.MenuSectionId;
import com.restaurant.menu.domain.model.ValidityPeriod;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.MenuJpaEntity;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.MenuSectionJpaEntity;
import java.util.ArrayList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = UUIDMapper.class)
public interface MenuPersistenceMapper {

    @Mapping(target = "startDate", source = "domain", qualifiedByName = "startDateFromValidity")
    @Mapping(target = "endDate", source = "domain", qualifiedByName = "endDateFromValidity")
    @Mapping(target = "sections", source = "domain.sections")
    MenuJpaEntity toJpaEntity(Menu domain);

    @Mapping(target = "validityPeriod", source = "jpaEntity", qualifiedByName = "validityFromJpa")
    @Mapping(target = "sections", source = "jpaEntity.sections")
    Menu toDomain(MenuJpaEntity jpaEntity);

    @Named("startDateFromValidity")
    default java.time.LocalDate startDateFromValidity(Menu domain) {
        return domain.getValidityPeriod() != null ? domain.getValidityPeriod().start() : null;
    }

    @Named("endDateFromValidity")
    default java.time.LocalDate endDateFromValidity(Menu domain) {
        return domain.getValidityPeriod() != null ? domain.getValidityPeriod().end() : null;
    }

    @Named("validityFromJpa")
    default ValidityPeriod validityFromJpa(MenuJpaEntity entity) {
        if (entity.getStartDate() == null && entity.getEndDate() == null) return null;
        return new ValidityPeriod(
            entity.getStartDate() != null ? entity.getStartDate() : java.time.LocalDate.now(),
            entity.getEndDate() != null ? entity.getEndDate() : java.time.LocalDate.now()
        );
    }

    default MenuSectionJpaEntity map(MenuSection section) {
        if (section == null) return null;
        MenuSectionJpaEntity entity = new MenuSectionJpaEntity();
        entity.setId(section.getId() != null ? section.getId().value() : null);
        entity.setCategoryId(section.getCategoryId() != null ? section.getCategoryId().value() : null);
        entity.setDisplayOrder(section.getDisplayOrder());
        return entity;
    }

    default MenuSection map(MenuSectionJpaEntity entity) {
        if (entity == null) return null;
        MenuSectionId id = entity.getId() != null ? new MenuSectionId(entity.getId()) : null;
        return new MenuSection(id, entity.getCategoryId() != null ? new com.restaurant.menu.domain.model.CategoryId(entity.getCategoryId()) : null, entity.getDisplayOrder());
    }

    default ArrayList<MenuSectionJpaEntity> mapSections(java.util.List<MenuSection> sections) {
        if (sections == null) return null;
        ArrayList<MenuSectionJpaEntity> result = new ArrayList<>();
        for (MenuSection s : sections) result.add(map(s));
        return result;
    }

    default ArrayList<MenuSection> mapSectionsJpa(java.util.List<MenuSectionJpaEntity> entities) {
        if (entities == null) return null;
        ArrayList<MenuSection> result = new ArrayList<>();
        for (MenuSectionJpaEntity e : entities) result.add(map(e));
        return result;
    }
}
