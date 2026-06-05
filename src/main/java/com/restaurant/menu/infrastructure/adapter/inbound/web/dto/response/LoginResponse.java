package com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
    String token,
    String email,
    String name,
    String role,
    @JsonProperty("expires_in") long expiresIn
) {}
