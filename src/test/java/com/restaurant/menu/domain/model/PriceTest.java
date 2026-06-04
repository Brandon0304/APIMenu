package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import com.restaurant.menu.domain.exception.InvalidPriceException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PriceTest {

    @Test
    void shouldCreatePriceWhenValueIsPositive() {
        Price price = new Price(new BigDecimal("25.50"));
        assertThat(price.value()).isEqualByComparingTo("25.50");
    }

    @Test
    void shouldCreatePriceWhenValueIsZero() {
        Price price = Price.zero();
        assertThat(price.value()).isEqualByComparingTo("0");
        assertThat(price.isZero()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThatThrownBy(() -> new Price(new BigDecimal("-10.00")))
            .isInstanceOf(InvalidPriceException.class);
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> new Price(null))
            .isInstanceOf(InvalidPriceException.class);
    }

    @Test
    void shouldRoundToTwoDecimals() {
        Price price = new Price(new BigDecimal("10.555"));
        assertThat(price.value()).isEqualByComparingTo("10.56");
    }

    @Test
    void shouldAddTwoPrices() {
        Price a = new Price(new BigDecimal("10.00"));
        Price b = new Price(new BigDecimal("5.50"));
        Price result = a.add(b);
        assertThat(result.value()).isEqualByComparingTo("15.50");
    }

    @Test
    void shouldMultiplyPrice() {
        Price price = new Price(new BigDecimal("10.00"));
        Price result = price.multiply(3);
        assertThat(result.value()).isEqualByComparingTo("30.00");
    }
}
