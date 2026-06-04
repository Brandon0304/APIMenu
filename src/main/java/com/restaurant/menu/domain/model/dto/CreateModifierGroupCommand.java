package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record CreateModifierGroupCommand(
    String name,
    String description,
    boolean required,
    int maxSelections
) {}
