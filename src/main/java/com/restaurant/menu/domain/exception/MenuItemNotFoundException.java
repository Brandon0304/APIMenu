package com.restaurant.menu.domain.exception;

import com.restaurant.menu.domain.model.MenuItemId;

public class MenuItemNotFoundException extends DomainException {

    public MenuItemNotFoundException(MenuItemId id) {
        super("MENU_ITEM_NOT_FOUND",
            "No menu item found with id: " + id.value(),
            404);
    }
}
