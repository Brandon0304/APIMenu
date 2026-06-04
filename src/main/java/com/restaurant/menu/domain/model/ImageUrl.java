package com.restaurant.menu.domain.model;

public record ImageUrl(String url) {

    public ImageUrl {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Image URL must not be blank");
        }
    }
}
