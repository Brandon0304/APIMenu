package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateIngredientRequest(
    @NotBlank String name,
    String description,
    @NotBlank String unit
) {}
