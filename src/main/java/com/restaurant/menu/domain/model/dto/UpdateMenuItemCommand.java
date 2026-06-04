package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record UpdateMenuItemCommand(
    String name,
    String description,
    Price price,
    CategoryId categoryId,
    PreparationTime preparationTime,
    ImageUrl imageUrl
) {}
