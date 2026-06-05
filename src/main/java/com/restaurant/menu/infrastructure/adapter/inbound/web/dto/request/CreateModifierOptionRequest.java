package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateModifierOptionRequest(
    @NotBlank String name,
    @NotNull BigDecimal priceAdjustment
) {}
