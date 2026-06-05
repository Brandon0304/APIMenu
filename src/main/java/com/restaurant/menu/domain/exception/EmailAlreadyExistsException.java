package com.restaurant.menu.domain.exception;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException() {
        super("EMAIL_ALREADY_EXISTS", "Email is already registered", 409);
    }
}
