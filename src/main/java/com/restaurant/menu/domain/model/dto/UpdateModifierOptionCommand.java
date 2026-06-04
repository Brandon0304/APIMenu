package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record UpdateModifierOptionCommand(
    String name,
    Price priceAdjustment
) {}
