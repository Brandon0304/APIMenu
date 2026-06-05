package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateMenuRequest(
    @NotBlank String name,
    String description,
    @NotNull LocalDate validFrom,
    @NotNull LocalDate validTo
) {}
