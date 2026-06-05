package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ModifierOptionResponse(
    UUID id,
    String name,
    BigDecimal priceAdjustment,
    boolean active
) {}
