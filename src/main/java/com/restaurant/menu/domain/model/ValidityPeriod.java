package com.restaurant.menu.domain.model;

import com.restaurant.menu.domain.exception.InvalidDateRangeException;
import java.time.LocalDate;

public record ValidityPeriod(LocalDate start, LocalDate end) {

    public ValidityPeriod {
        if (start == null) {
            throw new InvalidDateRangeException("Start date must not be null");
        }
        if (end == null) {
            throw new InvalidDateRangeException("End date must not be null");
        }
        if (end.isBefore(start)) {
            throw new InvalidDateRangeException(
                "End date (" + end + ") must not be before start date (" + start + ")"
            );
        }
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public boolean isActive() {
        return contains(LocalDate.now());
    }
}
