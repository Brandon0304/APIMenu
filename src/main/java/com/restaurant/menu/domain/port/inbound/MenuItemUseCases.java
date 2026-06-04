package com.restaurant.menu.domain.port.inbound;

import com.restaurant.menu.domain.model.*;
import com.restaurant.menu.domain.model.dto.*;
import java.util.List;

public interface MenuItemUseCases {
    MenuItemResult create(CreateMenuItemCommand command);
    MenuItemResult getById(MenuItemId id);
    List<MenuItemResult> getAll(int page, int size);
    List<MenuItemResult> getByCategoryId(CategoryId categoryId);
    MenuItemResult update(MenuItemId id, UpdateMenuItemCommand command);
    MenuItemResult updateStatus(MenuItemId id, boolean active);
    void delete(MenuItemId id);

    void addIngredient(MenuItemId menuItemId, IngredientId ingredientId, double quantity);
    void removeIngredient(MenuItemId menuItemId, IngredientId ingredientId);

    void assignModifierGroup(MenuItemId menuItemId, ModifierGroupId modifierGroupId);
    void removeModifierGroup(MenuItemId menuItemId, ModifierGroupId modifierGroupId);

    void addAllergen(MenuItemId menuItemId, AllergenId allergenId);
    void removeAllergen(MenuItemId menuItemId, AllergenId allergenId);

    NutritionalInfoResult updateNutritionalInfo(MenuItemId menuItemId, UpdateNutritionalInfoCommand command);
}
