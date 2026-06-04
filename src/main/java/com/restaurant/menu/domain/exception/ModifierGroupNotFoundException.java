package com.restaurant.menu.domain.exception;

import com.restaurant.menu.domain.model.ModifierGroupId;

public class ModifierGroupNotFoundException extends DomainException {

    public ModifierGroupNotFoundException(ModifierGroupId id) {
        super("MODIFIER_GROUP_NOT_FOUND",
            "No modifier group found with id: " + id.value(),
            404);
    }
}
