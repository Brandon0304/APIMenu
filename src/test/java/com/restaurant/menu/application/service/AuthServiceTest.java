package com.restaurant.menu.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.restaurant.menu.domain.dto.LoginCommand;
import com.restaurant.menu.domain.dto.LoginResult;
import com.restaurant.menu.domain.exception.InvalidCredentialsException;
import com.restaurant.menu.domain.exception.UserInactiveException;
import com.restaurant.menu.domain.model.User;
import com.restaurant.menu.domain.model.UserId;
import com.restaurant.menu.domain.model.UserRole;
import com.restaurant.menu.domain.port.outbound.UserRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private TokenService tokenService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder, tokenService);

        adminUser = User.create(
            new UserId(UUID.randomUUID()),
            "admin@test.com",
            passwordEncoder.encode("password123"),
            "Admin",
            UserRole.ADMIN
        );
    }

    @Test
    void shouldReturnLoginResultWhenCredentialsAreValid() {
        LoginCommand command = new LoginCommand("admin@test.com", "password123");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(tokenService.generateToken(adminUser)).thenReturn("jwt-token");
        when(tokenService.getExpirationSeconds()).thenReturn(3600L);

        LoginResult result = authService.login(command);

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.email()).isEqualTo("admin@test.com");
        assertThat(result.name()).isEqualTo("Admin");
        assertThat(result.role()).isEqualTo(UserRole.ADMIN);
        assertThat(result.expiresIn()).isEqualTo(3600L);
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        LoginCommand command = new LoginCommand("unknown@test.com", "password123");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(command))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {
        LoginCommand command = new LoginCommand("admin@test.com", "wrongpassword");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> authService.login(command))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        adminUser.deactivate();
        LoginCommand command = new LoginCommand("admin@test.com", "password123");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> authService.login(command))
            .isInstanceOf(UserInactiveException.class);
    }

    @Test
    void shouldTrimAndLowercaseEmail() {
        LoginCommand command = new LoginCommand("  Admin@Test.COM  ", "password123");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(tokenService.generateToken(adminUser)).thenReturn("jwt-token");
        when(tokenService.getExpirationSeconds()).thenReturn(3600L);

        LoginResult result = authService.login(command);

        assertThat(result.token()).isEqualTo("jwt-token");
        verify(userRepository).findByEmail("admin@test.com");
    }
}
