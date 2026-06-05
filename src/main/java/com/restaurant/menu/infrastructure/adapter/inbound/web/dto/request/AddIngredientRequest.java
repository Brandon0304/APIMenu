package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record AddIngredientRequest(
    @NotNull UUID ingredientId,
    @NotNull BigDecimal quantity
) {}
