package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MenuItemResponse(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    UUID categoryId,
    boolean active,
    Integer preparationTimeMinutes,
    String imageUrl,
    Instant createdAt,
    Instant updatedAt
) {}
