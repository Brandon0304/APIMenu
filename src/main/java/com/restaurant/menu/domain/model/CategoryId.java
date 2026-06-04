package com.restaurant.menu.domain.model;

import java.util.UUID;

public record CategoryId(UUID value) {
    public CategoryId {
        if (value == null) {
            throw new IllegalArgumentException("CategoryId must not be null");
        }
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }
}
