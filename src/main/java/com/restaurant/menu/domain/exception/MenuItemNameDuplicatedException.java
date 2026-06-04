package com.restaurant.menu.domain.exception;

import com.restaurant.menu.domain.model.CategoryId;

public class MenuItemNameDuplicatedException extends DomainException {

    public MenuItemNameDuplicatedException(String name, CategoryId categoryId) {
        super("MENU_ITEM_NAME_DUPLICATED",
            "A menu item with name '" + name + "' already exists in category " + categoryId.value(),
            409);
    }
}
