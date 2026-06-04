package com.restaurant.menu.domain.exception;

import com.restaurant.menu.domain.model.AllergenId;

public class AllergenNotFoundException extends DomainException {

    public AllergenNotFoundException(AllergenId id) {
        super("ALLERGEN_NOT_FOUND",
            "No allergen found with id: " + id.value(),
            404);
    }
}
