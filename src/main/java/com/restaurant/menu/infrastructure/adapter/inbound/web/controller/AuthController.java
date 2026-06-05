package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.LoginRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.RegisterRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.LoginResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.mapper.AuthApiMapper;
import com.restaurant.menu.shared.dto.ApiResponse;
import com.restaurant.menu.domain.port.inbound.AuthenticationUseCases;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationUseCases authUseCases;
    private final AuthApiMapper mapper;

    public AuthController(AuthenticationUseCases authUseCases, AuthApiMapper mapper) {
        this.authUseCases = authUseCases;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        var result = authUseCases.login(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.of(mapper.toResponse(result)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody @Valid RegisterRequest request) {
        var result = authUseCases.register(mapper.toRegisterCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of(mapper.toResponse(result)));
    }
}
