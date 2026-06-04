package com.restaurant.menu.domain.exception;

import com.restaurant.menu.domain.model.IngredientId;

public class IngredientNotFoundException extends DomainException {

    public IngredientNotFoundException(IngredientId id) {
        super("INGREDIENT_NOT_FOUND",
            "No ingredient found with id: " + id.value(),
            404);
    }
}
