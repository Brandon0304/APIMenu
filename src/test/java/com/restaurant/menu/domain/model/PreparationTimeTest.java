package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PreparationTimeTest {

    @Test
    void shouldCreateWhenMinutesArePositive() {
        PreparationTime time = new PreparationTime(15);
        assertThat(time.minutes()).isEqualTo(15);
    }

    @Test
    void shouldThrowWhenMinutesAreZero() {
        assertThatThrownBy(() -> new PreparationTime(0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenMinutesAreNegative() {
        assertThatThrownBy(() -> new PreparationTime(-5))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
