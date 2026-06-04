package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record CreateModifierOptionCommand(
    String name,
    Price priceAdjustment
) {}
