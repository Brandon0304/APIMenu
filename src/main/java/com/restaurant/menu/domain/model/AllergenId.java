package com.restaurant.menu.domain.model;

import java.util.UUID;

public record AllergenId(UUID value) {
    public AllergenId {
        if (value == null) {
            throw new IllegalArgumentException("AllergenId must not be null");
        }
    }

    public static AllergenId generate() {
        return new AllergenId(UUID.randomUUID());
    }
}
