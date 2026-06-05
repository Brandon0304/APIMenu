package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
    @NotBlank String name,
    String description,
    @Min(0) int displayOrder
) {}
