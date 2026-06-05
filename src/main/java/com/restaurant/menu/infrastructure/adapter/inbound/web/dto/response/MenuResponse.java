package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MenuResponse(
    UUID id,
    String name,
    String description,
    boolean active,
    LocalDate validFrom,
    LocalDate validTo,
    List<MenuSectionResponse> sections,
    Instant createdAt,
    Instant updatedAt
) {}
