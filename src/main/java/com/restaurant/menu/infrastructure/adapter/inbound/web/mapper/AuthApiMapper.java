package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.dto.LoginCommand;
import com.restaurant.menu.domain.dto.LoginResult;
import com.restaurant.menu.domain.dto.RegisterCommand;
import com.restaurant.menu.domain.model.UserRole;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.LoginRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.RegisterRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.LoginResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthApiMapper {

    public LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.email(), request.password());
    }

    public RegisterCommand toRegisterCommand(RegisterRequest request) {
        return new RegisterCommand(request.email(), request.password(), request.name(), UserRole.VIEWER);
    }

    public LoginResponse toResponse(LoginResult result) {
        return new LoginResponse(
            result.token(),
            result.email(),
            result.name(),
            result.role().name(),
            result.expiresIn()
        );
    }
}
