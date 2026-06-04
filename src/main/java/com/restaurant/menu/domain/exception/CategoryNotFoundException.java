package com.restaurant.menu.domain.exception;

import com.restaurant.menu.domain.model.CategoryId;

public class CategoryNotFoundException extends DomainException {

    public CategoryNotFoundException(CategoryId id) {
        super("CATEGORY_NOT_FOUND",
            "No category found with id: " + id.value(),
            404);
    }

    public CategoryNotFoundException(String name) {
        super("CATEGORY_NOT_FOUND",
            "No category found with name: " + name,
            404);
    }
}
