package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank @Email @Schema(example = "admin@restaurant.com") String email,
    @NotBlank @Schema(example = "admin123") String password
) {}
