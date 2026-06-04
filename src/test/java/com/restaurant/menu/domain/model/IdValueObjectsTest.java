package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class IdValueObjectsTest {

    @Test
    void shouldCreateCategoryId() {
        UUID uuid = UUID.randomUUID();
        CategoryId id = new CategoryId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void shouldRejectNullCategoryId() {
        assertThatThrownBy(() -> new CategoryId(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldGenerateCategoryId() {
        CategoryId id = CategoryId.generate();
        assertThat(id.value()).isNotNull();
    }

    @Test
    void shouldCreateMenuItemId() {
        UUID uuid = UUID.randomUUID();
        MenuItemId id = new MenuItemId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void shouldCreateMenuId() {
        assertThat(MenuId.generate().value()).isNotNull();
    }

    @Test
    void shouldCreateModifierGroupId() {
        assertThat(ModifierGroupId.generate().value()).isNotNull();
    }

    @Test
    void shouldCreateIngredientId() {
        assertThat(IngredientId.generate().value()).isNotNull();
    }

    @Test
    void shouldCreateAllergenId() {
        assertThat(AllergenId.generate().value()).isNotNull();
    }
}
