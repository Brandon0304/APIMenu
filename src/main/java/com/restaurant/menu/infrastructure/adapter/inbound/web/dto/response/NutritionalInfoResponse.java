package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.math.BigDecimal;

public record NutritionalInfoResponse(
    Integer calories,
    BigDecimal proteinGrams,
    BigDecimal carbsGrams,
    BigDecimal fatGrams,
    BigDecimal fiberGrams,
    Integer sodiumMg
) {}
