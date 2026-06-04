package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ImageUrlTest {

    @Test
    void shouldCreateWhenUrlIsValid() {
        ImageUrl url = new ImageUrl("https://example.com/image.jpg");
        assertThat(url.url()).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    void shouldThrowWhenUrlIsNull() {
        assertThatThrownBy(() -> new ImageUrl(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenUrlIsBlank() {
        assertThatThrownBy(() -> new ImageUrl("  "))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
