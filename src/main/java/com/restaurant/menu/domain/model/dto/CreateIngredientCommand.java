package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record CreateIngredientCommand(
    String name,
    String description,
    String unit
) {}
