package com.restaurant.menu.domain.exception;

public class InvalidDateRangeException extends DomainException {

    public InvalidDateRangeException(String message) {
        super("INVALID_DATE_RANGE", message, 422);
    }
}
