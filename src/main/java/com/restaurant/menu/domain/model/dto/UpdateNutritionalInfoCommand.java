package com.restaurant.menu.domain.model.dto;

import java.math.BigDecimal;

public record UpdateNutritionalInfoCommand(
    Integer calories,
    BigDecimal proteinGrams,
    BigDecimal carbsGrams,
    BigDecimal fatGrams,
    BigDecimal fiberGrams,
    Integer sodiumMg
) {}
