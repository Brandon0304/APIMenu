package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record ModifierOptionResult(
    ModifierOptionId id,
    String name,
    Price priceAdjustment,
    boolean active
) {}
