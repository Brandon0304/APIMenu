package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddMenuSectionRequest(
    @NotNull UUID categoryId,
    int displayOrder
) {}
