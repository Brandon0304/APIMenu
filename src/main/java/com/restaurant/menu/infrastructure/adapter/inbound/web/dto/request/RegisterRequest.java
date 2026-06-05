package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email @Schema(example = "user@restaurant.com") String email,
    @NotBlank @Size(min = 6, max = 100) @Schema(example = "mypassword") String password,
    @NotBlank @Schema(example = "John Doe") String name,
    @Schema(description = "User role. Available values: ADMIN, VIEWER, KITCHEN", example = "VIEWER", defaultValue = "VIEWER")
    String role
) {
}
