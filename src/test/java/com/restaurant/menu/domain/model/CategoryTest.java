package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void shouldCreateCategory() {
        Category category = Category.create("Entradas", "Appetizers", 1);
        assertThat(category.getId()).isNotNull();
        assertThat(category.getName()).isEqualTo("Entradas");
        assertThat(category.getDescription()).isEqualTo("Appetizers");
        assertThat(category.getDisplayOrder()).isEqualTo(1);
        assertThat(category.isActive()).isTrue();
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> Category.create("", "Desc", 1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullName() {
        assertThatThrownBy(() -> new Category(CategoryId.generate(), null, "Desc", 1, true))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNegativeDisplayOrder() {
        assertThatThrownBy(() -> Category.create("Test", "Desc", -1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldActivate() {
        Category category = new Category(CategoryId.generate(), "Test", null, 0, false);
        category.activate();
        assertThat(category.isActive()).isTrue();
    }

    @Test
    void shouldDeactivate() {
        Category category = new Category(CategoryId.generate(), "Test", null, 0, true);
        category.deactivate();
        assertThat(category.isActive()).isFalse();
    }
}
