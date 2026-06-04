package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IngredientTest {

    @Test
    void shouldCreateIngredient() {
        Ingredient ingredient = Ingredient.create("Tomato", "Fresh red tomatoes", "grams");
        assertThat(ingredient.getId()).isNotNull();
        assertThat(ingredient.getName()).isEqualTo("Tomato");
        assertThat(ingredient.isActive()).isTrue();
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> Ingredient.create("", "desc", "grams"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldToggleActive() {
        Ingredient ingredient = Ingredient.create("Cheese", null, null);
        assertThat(ingredient.isActive()).isTrue();
        ingredient.deactivate();
        assertThat(ingredient.isActive()).isFalse();
        ingredient.activate();
        assertThat(ingredient.isActive()).isTrue();
    }
}
