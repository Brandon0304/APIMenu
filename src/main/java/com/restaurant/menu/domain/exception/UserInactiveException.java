package com.restaurant.menu.domain.exception;

public class UserInactiveException extends DomainException {
    public UserInactiveException() {
        super("USER_INACTIVE", "User account is inactive", 401);
    }
}
