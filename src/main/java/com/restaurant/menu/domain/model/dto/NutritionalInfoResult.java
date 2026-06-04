package com.restaurant.menu.domain.model.dto;

import java.math.BigDecimal;

public record NutritionalInfoResult(
    Integer calories,
    BigDecimal proteinGrams,
    BigDecimal carbsGrams,
    BigDecimal fatGrams,
    BigDecimal fiberGrams,
    Integer sodiumMg
) {}
