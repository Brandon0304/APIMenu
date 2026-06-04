package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.CategoryId;
import java.time.Instant;

public record CategoryResult(
    CategoryId id,
    String name,
    String description,
    int displayOrder,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
