package com.restaurant.menu.domain.exception;

import com.restaurant.menu.domain.model.MenuId;

public class MenuNotFoundException extends DomainException {

    public MenuNotFoundException(MenuId id) {
        super("MENU_NOT_FOUND",
            "No menu found with id: " + id.value(),
            404);
    }
}
