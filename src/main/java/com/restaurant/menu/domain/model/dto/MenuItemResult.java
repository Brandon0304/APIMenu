package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;
import java.time.Instant;

public record MenuItemResult(
    MenuItemId id,
    String name,
    String description,
    Price price,
    CategoryId categoryId,
    boolean active,
    PreparationTime preparationTime,
    ImageUrl imageUrl,
    Instant createdAt,
    Instant updatedAt
) {}
