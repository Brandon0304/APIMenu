package com.restaurant.menu.domain.model;

import com.restaurant.menu.domain.exception.InvalidPriceException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Price(BigDecimal value) {

    public Price {
        if (value == null) {
            throw new InvalidPriceException("Price must not be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPriceException("Price must not be negative, got: " + value);
        }
        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public Price add(Price other) {
        return new Price(this.value.add(other.value));
    }

    public Price multiply(int quantity) {
        return new Price(this.value.multiply(BigDecimal.valueOf(quantity)));
    }

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static Price zero() {
        return new Price(BigDecimal.ZERO);
    }
}
