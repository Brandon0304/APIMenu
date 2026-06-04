package com.restaurant.menu.domain.model;

import java.util.UUID;

public record ModifierGroupId(UUID value) {
    public ModifierGroupId {
        if (value == null) {
            throw new IllegalArgumentException("ModifierGroupId must not be null");
        }
    }

    public static ModifierGroupId generate() {
        return new ModifierGroupId(UUID.randomUUID());
    }
}
