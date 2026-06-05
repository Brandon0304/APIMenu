package com.restaurant.menu.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException() {
        super("USER_NOT_FOUND", "User not found", 401);
    }
}
