package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.AllergenId;
import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.model.IngredientId;
import com.restaurant.menu.domain.model.MenuId;
import com.restaurant.menu.domain.model.MenuItemId;
import com.restaurant.menu.domain.model.MenuSectionId;
import com.restaurant.menu.domain.model.ModifierGroupId;
import com.restaurant.menu.domain.model.ModifierOptionId;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UUIDMapper {

    default UUID categoryIdToUuid(CategoryId id) {
        return id != null ? id.value() : null;
    }

    default CategoryId uuidToCategoryId(UUID uuid) {
        return uuid != null ? new CategoryId(uuid) : null;
    }

    default UUID menuItemIdToUuid(MenuItemId id) {
        return id != null ? id.value() : null;
    }

    default MenuItemId uuidToMenuItemId(UUID uuid) {
        return uuid != null ? new MenuItemId(uuid) : null;
    }

    default UUID menuIdToUuid(MenuId id) {
        return id != null ? id.value() : null;
    }

    default MenuId uuidToMenuId(UUID uuid) {
        return uuid != null ? new MenuId(uuid) : null;
    }

    default UUID modifierGroupIdToUuid(ModifierGroupId id) {
        return id != null ? id.value() : null;
    }

    default ModifierGroupId uuidToModifierGroupId(UUID uuid) {
        return uuid != null ? new ModifierGroupId(uuid) : null;
    }

    default UUID ingredientIdToUuid(IngredientId id) {
        return id != null ? id.value() : null;
    }

    default IngredientId uuidToIngredientId(UUID uuid) {
        return uuid != null ? new IngredientId(uuid) : null;
    }

    default UUID allergenIdToUuid(AllergenId id) {
        return id != null ? id.value() : null;
    }

    default AllergenId uuidToAllergenId(UUID uuid) {
        return uuid != null ? new AllergenId(uuid) : null;
    }

    default UUID menuSectionIdToUuid(MenuSectionId id) {
        return id != null ? id.value() : null;
    }

    default MenuSectionId uuidToMenuSectionId(UUID uuid) {
        return uuid != null ? new MenuSectionId(uuid) : null;
    }

    default UUID modifierOptionIdToUuid(ModifierOptionId id) {
        return id != null ? id.value() : null;
    }

    default ModifierOptionId uuidToModifierOptionId(UUID uuid) {
        return uuid != null ? new ModifierOptionId(uuid) : null;
    }
}
