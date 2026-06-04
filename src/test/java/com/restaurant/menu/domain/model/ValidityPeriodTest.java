package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import com.restaurant.menu.domain.exception.InvalidDateRangeException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ValidityPeriodTest {

    @Test
    void shouldCreateWhenEndIsAfterStart() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        ValidityPeriod period = new ValidityPeriod(start, end);
        assertThat(period.start()).isEqualTo(start);
        assertThat(period.end()).isEqualTo(end);
    }

    @Test
    void shouldCreateWhenEndEqualsStart() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        ValidityPeriod period = new ValidityPeriod(date, date);
        assertThat(period.start()).isEqualTo(date);
    }

    @Test
    void shouldThrowWhenEndIsBeforeStart() {
        assertThatThrownBy(() -> new ValidityPeriod(
            LocalDate.of(2026, 12, 31),
            LocalDate.of(2026, 1, 1)
        )).isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void shouldThrowWhenStartIsNull() {
        assertThatThrownBy(() -> new ValidityPeriod(null, LocalDate.now()))
            .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void shouldContainDateWithinRange() {
        ValidityPeriod period = new ValidityPeriod(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
        );
        assertThat(period.contains(LocalDate.of(2026, 6, 15))).isTrue();
    }

    @Test
    void shouldNotContainDateBeforeStart() {
        ValidityPeriod period = new ValidityPeriod(
            LocalDate.of(2026, 6, 1),
            LocalDate.of(2026, 12, 31)
        );
        assertThat(period.contains(LocalDate.of(2026, 5, 31))).isFalse();
    }
}
