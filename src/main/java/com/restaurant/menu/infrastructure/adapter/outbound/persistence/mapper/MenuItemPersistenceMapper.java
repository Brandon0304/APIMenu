package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.Ingredient;
import com.restaurant.menu.domain.model.IngredientQuantity;
import com.restaurant.menu.domain.model.MenuItem;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.MenuItemAllergenJpaEntity;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.IngredientJpaEntity;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.MenuItemIngredientJpaEntity;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.MenuItemJpaEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MenuItemPersistenceMapper {

    private final UUIDMapper uuidMapper;
    private final ValueObjectMapper valueObjectMapper;

    public MenuItemPersistenceMapper(UUIDMapper uuidMapper, ValueObjectMapper valueObjectMapper) {
        this.uuidMapper = uuidMapper;
        this.valueObjectMapper = valueObjectMapper;
    }

    public MenuItemJpaEntity toJpaEntity(MenuItem domain) {
        if (domain == null) return null;
        MenuItemJpaEntity entity = new MenuItemJpaEntity();
        entity.setId(uuidMapper.menuItemIdToUuid(domain.getId()));
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(valueObjectMapper.mapPriceToBigDecimal(domain.getPrice()));
        entity.setImageUrl(valueObjectMapper.mapImageUrlToString(domain.getImageUrl()));
        entity.setPreparationTimeMinutes(valueObjectMapper.mapPreparationTimeToInt(domain.getPreparationTime()));
        entity.setActive(domain.isActive());
        if (domain.getCategoryId() != null) {
            entity.setCategoryId(uuidMapper.categoryIdToUuid(domain.getCategoryId()));
        }
        return entity;
    }

    public MenuItem toDomain(MenuItemJpaEntity entity) {
        if (entity == null) return null;
        return new MenuItem(
            uuidMapper.uuidToMenuItemId(entity.getId()),
            entity.getName(),
            entity.getDescription(),
            valueObjectMapper.mapBigDecimalToPrice(entity.getPrice()),
            uuidMapper.uuidToCategoryId(entity.getCategoryId()),
            valueObjectMapper.mapIntToPreparationTime(entity.getPreparationTimeMinutes()),
            valueObjectMapper.mapStringToImageUrl(entity.getImageUrl()),
            entity.isActive(),
            mapIngredientQuantities(entity.getIngredients()),
            null,
            null,
            valueObjectMapper.toDomain(entity.getNutritionalInfo())
        );
    }

    public List<MenuItemIngredientJpaEntity> mapIngredients(MenuItem domain) {
        if (domain.getIngredients() == null) return new ArrayList<>();
        return domain.getIngredients().stream()
            .map(iq -> {
                MenuItemIngredientJpaEntity entity = new MenuItemIngredientJpaEntity();
                UUID ingredientId = uuidMapper.ingredientIdToUuid(iq.getIngredient().getId());
                entity.setIngredientId(ingredientId);
                IngredientJpaEntity ingRef = new IngredientJpaEntity();
                ingRef.setId(ingredientId);
                entity.setIngredient(ingRef);
                entity.setQuantity(BigDecimal.valueOf(iq.getQuantity()));
                return entity;
            })
            .collect(Collectors.toList());
    }

    public List<MenuItemIngredientJpaEntity> updateIngredientEntities(MenuItemJpaEntity itemEntity, MenuItem domain) {
        List<MenuItemIngredientJpaEntity> ingredients = mapIngredients(domain);
        for (MenuItemIngredientJpaEntity ing : ingredients) {
            ing.setMenuItemId(itemEntity.getId());
        }
        return ingredients;
    }

    public List<MenuItemAllergenJpaEntity> mapAllergens(MenuItem domain, UUID menuItemId) {
        if (domain.getAllergens() == null) return new ArrayList<>();
        return domain.getAllergens().stream()
            .map(a -> new MenuItemAllergenJpaEntity(menuItemId, uuidMapper.allergenIdToUuid(a.getId())))
            .collect(Collectors.toList());
    }

    public List<UUID> mapModifierGroupIds(MenuItem domain) {
        if (domain.getModifierGroups() == null) return new ArrayList<>();
        return domain.getModifierGroups().stream()
            .map(mg -> uuidMapper.modifierGroupIdToUuid(mg.getId()))
            .collect(Collectors.toList());
    }

    private List<IngredientQuantity> mapIngredientQuantities(List<MenuItemIngredientJpaEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
            .map(e -> {
                Ingredient ingredient = mapIngredient(e.getIngredient());
                return new IngredientQuantity(ingredient, e.getQuantity().doubleValue());
            })
            .collect(Collectors.toList());
    }

    private Ingredient mapIngredient(com.restaurant.menu.infrastructure.adapter.outbound.persistence.IngredientJpaEntity entity) {
        if (entity == null) return null;
        return new Ingredient(
            uuidMapper.uuidToIngredientId(entity.getId()),
            entity.getName(),
            entity.getDescription(),
            entity.isActive(),
            entity.getUnit()
        );
    }
}
