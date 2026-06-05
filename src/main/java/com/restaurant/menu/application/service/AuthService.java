package com.restaurant.menu.application.service;

import com.restaurant.menu.domain.dto.LoginCommand;
import com.restaurant.menu.domain.dto.LoginResult;
import com.restaurant.menu.domain.dto.RegisterCommand;
import com.restaurant.menu.domain.exception.EmailAlreadyExistsException;
import com.restaurant.menu.domain.exception.InvalidCredentialsException;
import com.restaurant.menu.domain.exception.UserInactiveException;
import com.restaurant.menu.domain.model.User;
import com.restaurant.menu.domain.model.UserId;
import com.restaurant.menu.domain.port.inbound.AuthenticationUseCases;
import com.restaurant.menu.domain.port.outbound.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService implements AuthenticationUseCases {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email().trim().toLowerCase())
            .orElseThrow(InvalidCredentialsException::new);

        if (!user.active()) {
            throw new UserInactiveException();
        }

        if (!passwordEncoder.matches(command.password(), user.password())) {
            throw new InvalidCredentialsException();
        }

        String token = tokenService.generateToken(user);
        long expiresIn = tokenService.getExpirationSeconds();

        return new LoginResult(token, user.email(), user.name(), user.role(), expiresIn);
    }

    @Override
    public LoginResult register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email().trim().toLowerCase())) {
            throw new EmailAlreadyExistsException();
        }

        User user = User.create(
            UserId.generate(),
            command.email(),
            passwordEncoder.encode(command.password()),
            command.name(),
            command.role()
        );

        User saved = userRepository.save(user);
        String token = tokenService.generateToken(saved);
        long expiresIn = tokenService.getExpirationSeconds();

        return new LoginResult(token, saved.email(), saved.name(), saved.role(), expiresIn);
    }
}
