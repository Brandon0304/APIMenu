package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.*;

public record MenuSectionResult(
    MenuSectionId id,
    CategoryId categoryId,
    int displayOrder
) {}
