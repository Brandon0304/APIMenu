package com.restaurant.menu.domain.dto;

import com.restaurant.menu.domain.model.UserRole;

public record RegisterCommand(String email, String password, String name, UserRole role) {
}
