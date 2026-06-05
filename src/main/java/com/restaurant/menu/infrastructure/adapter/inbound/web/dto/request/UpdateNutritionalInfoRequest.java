package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateNutritionalInfoRequest(
    Integer calories,
    BigDecimal proteinGrams,
    BigDecimal carbsGrams,
    BigDecimal fatGrams,
    BigDecimal fiberGrams,
    Integer sodiumMg
) {}
