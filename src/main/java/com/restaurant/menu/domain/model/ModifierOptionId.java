package com.restaurant.menu.domain.model;

import java.util.UUID;

public record ModifierOptionId(UUID value) {
    public ModifierOptionId {
        if (value == null) {
            throw new IllegalArgumentException("ModifierOptionId must not be null");
        }
    }

    public static ModifierOptionId generate() {
        return new ModifierOptionId(UUID.randomUUID());
    }
}
