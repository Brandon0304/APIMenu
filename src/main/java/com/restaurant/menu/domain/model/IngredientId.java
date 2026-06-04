package com.restaurant.menu.domain.model;

import java.util.UUID;

public record IngredientId(UUID value) {
    public IngredientId {
        if (value == null) {
            throw new IllegalArgumentException("IngredientId must not be null");
        }
    }

    public static IngredientId generate() {
        return new IngredientId(UUID.randomUUID());
    }
}
