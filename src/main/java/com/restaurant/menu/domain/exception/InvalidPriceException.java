package com.restaurant.menu.domain.exception;

public class InvalidPriceException extends DomainException {

    public InvalidPriceException(String message) {
        super("INVALID_PRICE", message, 422);
    }
}
