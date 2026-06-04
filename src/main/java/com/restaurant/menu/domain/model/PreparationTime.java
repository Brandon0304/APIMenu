package com.restaurant.menu.domain.model;

public record PreparationTime(int minutes) {

    public PreparationTime {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Preparation time must be positive, got: " + minutes);
        }
    }
}
