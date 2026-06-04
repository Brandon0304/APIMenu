package com.restaurant.menu.domain.model;

import java.util.UUID;

public record MenuSectionId(UUID value) {
    public MenuSectionId {
        if (value == null) {
            throw new IllegalArgumentException("MenuSectionId must not be null");
        }
    }

    public static MenuSectionId generate() {
        return new MenuSectionId(UUID.randomUUID());
    }
}
