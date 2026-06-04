package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import com.restaurant.menu.domain.exception.*;
import org.junit.jupiter.api.Test;

class DomainExceptionTest {

    @Test
    void shouldCreateCategoryNotFoundException() {
        CategoryId id = CategoryId.generate();
        DomainException ex = new CategoryNotFoundException(id);
        assertThat(ex.getCode()).isEqualTo("CATEGORY_NOT_FOUND");
        assertThat(ex.getHttpStatus()).isEqualTo(404);
    }

    @Test
    void shouldCreateCategoryNameDuplicatedException() {
        DomainException ex = new CategoryNameDuplicatedException("Entradas");
        assertThat(ex.getCode()).isEqualTo("CATEGORY_NAME_DUPLICATED");
        assertThat(ex.getHttpStatus()).isEqualTo(409);
    }

    @Test
    void shouldCreateMenuItemNotFoundException() {
        DomainException ex = new MenuItemNotFoundException(MenuItemId.generate());
        assertThat(ex.getCode()).isEqualTo("MENU_ITEM_NOT_FOUND");
    }

    @Test
    void shouldCreateInvalidPriceException() {
        DomainException ex = new InvalidPriceException("Price must be positive");
        assertThat(ex.getCode()).isEqualTo("INVALID_PRICE");
        assertThat(ex.getHttpStatus()).isEqualTo(422);
    }

    @Test
    void shouldCreateInvalidDateRangeException() {
        DomainException ex = new InvalidDateRangeException("Invalid date range");
        assertThat(ex.getCode()).isEqualTo("INVALID_DATE_RANGE");
    }

    @Test
    void shouldCreateMenuNotFoundException() {
        DomainException ex = new MenuNotFoundException(MenuId.generate());
        assertThat(ex.getCode()).isEqualTo("MENU_NOT_FOUND");
    }

    @Test
    void shouldCreateModifierGroupNotFoundException() {
        DomainException ex = new ModifierGroupNotFoundException(ModifierGroupId.generate());
        assertThat(ex.getCode()).isEqualTo("MODIFIER_GROUP_NOT_FOUND");
    }

    @Test
    void shouldCreateIngredientNotFoundException() {
        DomainException ex = new IngredientNotFoundException(IngredientId.generate());
        assertThat(ex.getCode()).isEqualTo("INGREDIENT_NOT_FOUND");
    }

    @Test
    void shouldCreateAllergenNotFoundException() {
        DomainException ex = new AllergenNotFoundException(AllergenId.generate());
        assertThat(ex.getCode()).isEqualTo("ALLERGEN_NOT_FOUND");
    }

    @Test
    void shouldCreateMaxSelectionsExceededException() {
        DomainException ex = new MaxSelectionsExceededException(5, 3);
        assertThat(ex.getCode()).isEqualTo("MAX_SELECTIONS_EXCEEDED");
    }
}
