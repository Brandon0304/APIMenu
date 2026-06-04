package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;
import java.util.List;

public record ModifierGroupResult(
    ModifierGroupId id,
    String name,
    String description,
    boolean required,
    int maxSelections,
    boolean active,
    List<ModifierOptionResult> options
) {}
