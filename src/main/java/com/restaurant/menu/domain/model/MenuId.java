package com.restaurant.menu.domain.model;

import java.util.UUID;

public record MenuId(UUID value) {
    public MenuId {
        if (value == null) {
            throw new IllegalArgumentException("MenuId must not be null");
        }
    }

    public static MenuId generate() {
        return new MenuId(UUID.randomUUID());
    }
}
