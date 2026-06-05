package com.restaurant.menu.infrastructure.adapter.inbound.web.handler;

import static org.assertj.core.api.Assertions.*;

import com.restaurant.menu.domain.exception.CategoryNotFoundException;
import com.restaurant.menu.domain.exception.DomainException;
import com.restaurant.menu.domain.exception.InvalidCredentialsException;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void shouldHandleDomainException() {
        DomainException ex = new CategoryNotFoundException("test-id");

        ProblemDetail result = handler.handleDomainException(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("CATEGORY_NOT_FOUND");
        assertThat(result.getDetail()).contains("test-id");
        assertThat(result.getProperties()).containsKey("code");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    @Test
    void shouldHandleInvalidCredentialsException() {
        DomainException ex = new InvalidCredentialsException();

        ProblemDetail result = handler.handleDomainException(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(401);
        assertThat(result.getTitle()).isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    void shouldHandleAccessDeniedException() {
        var ex = new AccessDeniedException("Access denied");

        ProblemDetail result = handler.handleAccessDenied(ex);

        assertThat(result.getStatus()).isEqualTo(403);
        assertThat(result.getTitle()).isEqualTo("FORBIDDEN");
    }

    @Test
    void shouldHandleAuthenticationException() {
        var ex = new BadCredentialsException("Bad credentials");

        ProblemDetail result = handler.handleAuthenticationError(ex);

        assertThat(result.getStatus()).isEqualTo(401);
        assertThat(result.getTitle()).isEqualTo("UNAUTHORIZED");
    }

    @Test
    void shouldHandleValidationException() {
        var ex = new MethodArgumentNotValidException(null,
            createBindingResultWithErrors());

        ProblemDetail result = handler.handleValidationError(ex);

        assertThat(result.getStatus()).isEqualTo(422);
        assertThat(result.getTitle()).isEqualTo("VALIDATION_ERROR");
        assertThat(result.getProperties()).containsKey("errors");
    }

    @Test
    void shouldHandleGenericException() {
        var ex = new RuntimeException("Unexpected");

        ProblemDetail result = handler.handleGenericError(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("INTERNAL_ERROR");
        assertThat(result.getDetail()).isEqualTo("An unexpected error occurred");
    }

    private BindingResult createBindingResultWithErrors() {
        var bindingResult = new org.springframework.validation.BeanPropertyBindingResult(new Object(), "test");
        bindingResult.addError(new FieldError("test", "name", "must not be blank"));
        return bindingResult;
    }
}
