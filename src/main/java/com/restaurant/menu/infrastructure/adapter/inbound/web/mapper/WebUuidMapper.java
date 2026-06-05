package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.model.AllergenId;
import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.model.IngredientId;
import com.restaurant.menu.domain.model.MenuId;
import com.restaurant.menu.domain.model.MenuItemId;
import com.restaurant.menu.domain.model.MenuSectionId;
import com.restaurant.menu.domain.model.ModifierGroupId;
import com.restaurant.menu.domain.model.ModifierOptionId;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class WebUuidMapper {

    public UUID toUuid(CategoryId id) { return id != null ? id.value() : null; }
    public CategoryId toCategoryId(UUID uuid) { return uuid != null ? new CategoryId(uuid) : null; }

    public UUID toUuid(MenuItemId id) { return id != null ? id.value() : null; }
    public MenuItemId toMenuItemId(UUID uuid) { return uuid != null ? new MenuItemId(uuid) : null; }

    public UUID toUuid(IngredientId id) { return id != null ? id.value() : null; }
    public IngredientId toIngredientId(UUID uuid) { return uuid != null ? new IngredientId(uuid) : null; }

    public UUID toUuid(ModifierGroupId id) { return id != null ? id.value() : null; }
    public ModifierGroupId toModifierGroupId(UUID uuid) { return uuid != null ? new ModifierGroupId(uuid) : null; }

    public UUID toUuid(ModifierOptionId id) { return id != null ? id.value() : null; }
    public ModifierOptionId toModifierOptionId(UUID uuid) { return uuid != null ? new ModifierOptionId(uuid) : null; }

    public UUID toUuid(MenuId id) { return id != null ? id.value() : null; }
    public MenuId toMenuId(UUID uuid) { return uuid != null ? new MenuId(uuid) : null; }

    public UUID toUuid(MenuSectionId id) { return id != null ? id.value() : null; }
    public MenuSectionId toMenuSectionId(UUID uuid) { return uuid != null ? new MenuSectionId(uuid) : null; }

    public UUID toUuid(AllergenId id) { return id != null ? id.value() : null; }
    public AllergenId toAllergenId(UUID uuid) { return uuid != null ? new AllergenId(uuid) : null; }
}
