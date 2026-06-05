package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.util.UUID;

public record MenuSectionResponse(
    UUID id,
    UUID categoryId,
    int displayOrder
) {}
