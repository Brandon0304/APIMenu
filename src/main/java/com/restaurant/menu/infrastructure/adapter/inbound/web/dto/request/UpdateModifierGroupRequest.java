package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UpdateModifierGroupRequest(
    @NotBlank String name,
    String description,
    boolean required,
    @Positive int maxSelections
) {}
