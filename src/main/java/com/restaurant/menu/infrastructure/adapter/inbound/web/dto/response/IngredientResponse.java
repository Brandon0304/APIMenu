package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.util.UUID;

public record IngredientResponse(
    UUID id,
    String name,
    String description,
    boolean active,
    String unit
) {}
