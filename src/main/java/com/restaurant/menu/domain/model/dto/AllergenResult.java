package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record AllergenResult(
    AllergenId id,
    String name,
    String description,
    String icon
) {}
