package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.util.UUID;

public record AllergenResponse(
    UUID id,
    String name,
    String description,
    String icon
) {}
