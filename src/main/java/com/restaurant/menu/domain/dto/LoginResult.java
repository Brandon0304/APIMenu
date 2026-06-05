package com.restaurant.menu.domain.dto;

import com.restaurant.menu.domain.model.UserRole;

public record LoginResult(String token, String email, String name, UserRole role, long expiresIn) {
}
