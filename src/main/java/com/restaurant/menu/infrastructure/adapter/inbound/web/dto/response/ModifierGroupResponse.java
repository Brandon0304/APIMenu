package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.util.List;
import java.util.UUID;

public record ModifierGroupResponse(
    UUID id,
    String name,
    String description,
    boolean required,
    int maxSelections,
    boolean active,
    List<ModifierOptionResponse> options
) {}
