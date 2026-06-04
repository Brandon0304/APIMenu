package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuItemTest {

    private CategoryId categoryId;
    private Price basePrice;

    @BeforeEach
    void setUp() {
        categoryId = CategoryId.generate();
        basePrice = new Price(new BigDecimal("20.00"));
    }

    @Test
    void shouldCreateMenuItem() {
        MenuItem item = MenuItem.create(
            "Pizza Margherita", "Classic pizza",
            basePrice, categoryId,
            new PreparationTime(20), new ImageUrl("https://example.com/pizza.jpg")
        );
        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo("Pizza Margherita");
        assertThat(item.isActive()).isTrue();
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> MenuItem.create(
            "", "desc", basePrice, categoryId, null, null
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullPrice() {
        assertThatThrownBy(() -> new MenuItem(
            MenuItemId.generate(), "Test", "desc", null, categoryId,
            null, null, true, null, null, null, null
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldAddIngredient() {
        MenuItem item = createBasicItem();
        Ingredient tomato = Ingredient.create("Tomato", null, null);
        item.addIngredient(tomato, 2.0);
        assertThat(item.getIngredients()).hasSize(1);
        assertThat(item.getIngredients().get(0).getQuantity()).isEqualTo(2.0);
    }

    @Test
    void shouldUpdateQuantityWhenAddingSameIngredient() {
        MenuItem item = createBasicItem();
        Ingredient tomato = Ingredient.create("Tomato", null, null);
        item.addIngredient(tomato, 2.0);
        item.addIngredient(tomato, 1.5);
        assertThat(item.getIngredients()).hasSize(1);
        assertThat(item.getIngredients().get(0).getQuantity()).isEqualTo(3.5);
    }

    @Test
    void shouldRemoveIngredient() {
        MenuItem item = createBasicItem();
        Ingredient tomato = Ingredient.create("Tomato", null, null);
        item.addIngredient(tomato, 2.0);
        item.removeIngredient(tomato.getId());
        assertThat(item.getIngredients()).isEmpty();
    }

    @Test
    void shouldCalculateFinalPriceWithModifiers() {
        MenuItem item = createBasicItem();
        ModifierGroup mandatoryGroup = new ModifierGroup(
            ModifierGroupId.generate(), "Size", null, true, 1, true,
            java.util.List.of(ModifierOption.create("Large", new Price(new BigDecimal("3.00"))))
        );
        item.assignModifierGroup(mandatoryGroup);
        Price finalPrice = item.calculateFinalPrice();
        assertThat(finalPrice.value()).isEqualByComparingTo("23.00");
    }

    @Test
    void shouldNotAssignDuplicateModifierGroup() {
        MenuItem item = createBasicItem();
        ModifierGroup group = ModifierGroup.create("Extras", null, false, 1);
        item.assignModifierGroup(group);
        assertThatThrownBy(() -> item.assignModifierGroup(group))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldToggleActive() {
        MenuItem item = createBasicItem();
        assertThat(item.isActive()).isTrue();
        item.deactivate();
        assertThat(item.isActive()).isFalse();
        item.activate();
        assertThat(item.isActive()).isTrue();
    }

    private MenuItem createBasicItem() {
        return MenuItem.create("Test Item", "Description",
            basePrice, categoryId,
            new PreparationTime(10), new ImageUrl("https://example.com/img.jpg"));
    }
}
