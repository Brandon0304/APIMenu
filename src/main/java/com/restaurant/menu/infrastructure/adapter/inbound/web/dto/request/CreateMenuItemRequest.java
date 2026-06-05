package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateMenuItemRequest(
    @NotBlank String name,
    String description,
    @NotNull BigDecimal price,
    @NotNull UUID categoryId,
    Integer preparationTimeMinutes,
    String imageUrl
) {}
