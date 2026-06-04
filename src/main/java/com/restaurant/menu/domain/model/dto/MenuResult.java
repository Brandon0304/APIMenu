package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;
import java.time.Instant;
import java.util.List;

public record MenuResult(
    MenuId id,
    String name,
    String description,
    boolean active,
    ValidityPeriod validityPeriod,
    List<MenuSectionResult> sections,
    Instant createdAt,
    Instant updatedAt
) {}
