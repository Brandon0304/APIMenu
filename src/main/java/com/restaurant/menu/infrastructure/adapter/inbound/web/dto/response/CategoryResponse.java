package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String name,
    String description,
    int displayOrder,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
