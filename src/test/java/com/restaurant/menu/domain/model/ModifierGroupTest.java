package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ModifierGroupTest {

    @Test
    void shouldCreateModifierGroup() {
        ModifierGroup group = ModifierGroup.create("Size", "Choose size", false, 1);
        assertThat(group.getId()).isNotNull();
        assertThat(group.getName()).isEqualTo("Size");
        assertThat(group.isRequired()).isFalse();
    }

    @Test
    void shouldAddOption() {
        ModifierGroup group = ModifierGroup.create("Extras", "Extra toppings", false, 3);
        group.addOption(ModifierOption.create("Cheese", new Price(new BigDecimal("1.50"))));
        assertThat(group.getOptions()).hasSize(1);
    }

    @Test
    void shouldThrowWhenMaxSelectionsIsZero() {
        assertThatThrownBy(() -> ModifierGroup.create("Test", null, false, 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requiredGroupMustHaveAtLeastOneOption() {
        assertThatThrownBy(() -> new ModifierGroup(
            ModifierGroupId.generate(), "Required", null, true, 1, true, new ArrayList<>()
        )).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldCalculatePriceImpact() {
        ModifierGroup group = ModifierGroup.create("Extras", null, false, 3);
        group.addOption(ModifierOption.create("Cheese", new Price(new BigDecimal("2.00"))));
        group.addOption(ModifierOption.create("Bacon", new Price(new BigDecimal("3.00"))));
        Price impact = group.calculatePriceImpact();
        assertThat(impact.value()).isEqualByComparingTo("5.00");
    }

    @Test
    void shouldCalculateZeroPriceImpactWhenNoOptions() {
        ModifierGroup group = ModifierGroup.create("Free", null, false, 1);
        Price impact = group.calculatePriceImpact();
        assertThat(impact.isZero()).isTrue();
    }
}
