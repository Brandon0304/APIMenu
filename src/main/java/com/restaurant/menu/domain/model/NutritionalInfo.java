package com.restaurant.menu.domain.model;

import java.math.BigDecimal;

public record NutritionalInfo(
    Integer calories,
    BigDecimal proteinGrams,
    BigDecimal carbsGrams,
    BigDecimal fatGrams,
    BigDecimal fiberGrams,
    Integer sodiumMg
) {
    public NutritionalInfo {
        if (calories != null && calories < 0) {
            throw new IllegalArgumentException("Calories must not be negative, got: " + calories);
        }
        if (sodiumMg != null && sodiumMg < 0) {
            throw new IllegalArgumentException("Sodium must not be negative, got: " + sodiumMg);
        }
    }
}
