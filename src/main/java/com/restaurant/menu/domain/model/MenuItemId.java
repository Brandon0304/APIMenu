package com.restaurant.menu.domain.model;

import java.util.UUID;

public record MenuItemId(UUID value) {
    public MenuItemId {
        if (value == null) {
            throw new IllegalArgumentException("MenuItemId must not be null");
        }
    }

    public static MenuItemId generate() {
        return new MenuItemId(UUID.randomUUID());
    }
}
