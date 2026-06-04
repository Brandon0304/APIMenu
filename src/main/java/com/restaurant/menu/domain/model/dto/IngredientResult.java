package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record IngredientResult(
    IngredientId id,
    String name,
    String description,
    boolean active,
    String unit
) {}
