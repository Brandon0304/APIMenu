package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NutritionalInfoTest {

    @Test
    void shouldCreateWithAllFields() {
        NutritionalInfo info = new NutritionalInfo(250, new BigDecimal("10.5"),
            new BigDecimal("30.0"), new BigDecimal("8.0"),
            new BigDecimal("2.0"), 500);
        assertThat(info.calories()).isEqualTo(250);
        assertThat(info.proteinGrams()).isEqualByComparingTo("10.5");
    }

    @Test
    void shouldCreateWithNullOptionalFields() {
        NutritionalInfo info = new NutritionalInfo(null, null, null, null, null, null);
        assertThat(info.calories()).isNull();
    }

    @Test
    void shouldThrowWhenCaloriesNegative() {
        assertThatThrownBy(() -> new NutritionalInfo(-1, null, null, null, null, null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenSodiumNegative() {
        assertThatThrownBy(() -> new NutritionalInfo(null, null, null, null, null, -1))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
