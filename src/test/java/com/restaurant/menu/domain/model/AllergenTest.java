package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AllergenTest {

    @Test
    void shouldCreateAllergen() {
        Allergen allergen = Allergen.create("Gluten", "Contains gluten", "wheat-icon");
        assertThat(allergen.getId()).isNotNull();
        assertThat(allergen.getName()).isEqualTo("Gluten");
        assertThat(allergen.getIcon()).isEqualTo("wheat-icon");
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> Allergen.create("", "desc", null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
