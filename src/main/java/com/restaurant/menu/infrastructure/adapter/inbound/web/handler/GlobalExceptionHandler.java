package com.restaurant.menu.infrastructure.adapter.inbound.web.handler;

import com.restaurant.menu.domain.exception.DomainException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(ex.getHttpStatus()), ex.getMessage());
        problem.setTitle(ex.getCode());
        problem.setType(URI.create("https://api.restaurant.com/errors/" + ex.getCode().toLowerCase()));
        problem.setProperty("code", ex.getCode());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationError(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, "The request contains invalid fields");
        problem.setTitle("VALIDATION_ERROR");
        problem.setType(URI.create("https://api.restaurant.com/errors/validation-error"));
        problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("timestamp", Instant.now());

        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ValidationError(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
            .collect(Collectors.toList());
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericError(Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("INTERNAL_ERROR");
        problem.setType(URI.create("https://api.restaurant.com/errors/internal-error"));
        problem.setProperty("code", "INTERNAL_ERROR");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    record ValidationError(String field, String message, Object rejectedValue) {}
}
