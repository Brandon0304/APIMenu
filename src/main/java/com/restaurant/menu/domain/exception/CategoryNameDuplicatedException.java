package com.restaurant.menu.domain.exception;

public class CategoryNameDuplicatedException extends DomainException {

    public CategoryNameDuplicatedException(String name) {
        super("CATEGORY_NAME_DUPLICATED",
            "A category with name '" + name + "' already exists",
            409);
    }
}
