package com.restaurant.menu.domain.model.dto;

import com.restaurant.menu.domain.model.CategoryId;

public record UpdateCategoryCommand(
    String name,
    String description,
    int displayOrder
) {}
