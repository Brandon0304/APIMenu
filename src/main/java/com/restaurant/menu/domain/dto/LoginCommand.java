package com.restaurant.menu.domain.dto;

import com.restaurant.menu.domain.model.UserRole;

public record LoginCommand(String email, String password) {
}
