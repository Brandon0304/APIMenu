package com.restaurant.menu.domain.model;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId must not be null");
        }
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}
