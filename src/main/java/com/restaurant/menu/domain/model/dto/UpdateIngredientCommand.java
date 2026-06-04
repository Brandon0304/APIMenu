package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record UpdateIngredientCommand(
    String name,
    String description,
    String unit
) {}
