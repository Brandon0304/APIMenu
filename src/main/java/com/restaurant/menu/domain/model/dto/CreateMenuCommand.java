package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record CreateMenuCommand(
    String name,
    String description,
    ValidityPeriod validityPeriod
) {}
