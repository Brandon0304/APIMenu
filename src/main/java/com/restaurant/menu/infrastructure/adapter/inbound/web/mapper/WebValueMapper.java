package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.model.ImageUrl;
import com.restaurant.menu.domain.model.PreparationTime;
import com.restaurant.menu.domain.model.Price;
import com.restaurant.menu.domain.model.ValidityPeriod;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class WebValueMapper {

    public BigDecimal toBigDecimal(Price price) {
        return price != null ? price.value() : null;
    }

    public Price toPrice(BigDecimal value) {
        return value != null ? new Price(value) : null;
    }

    public Integer toInt(PreparationTime time) {
        return time != null ? time.minutes() : null;
    }

    public PreparationTime toPreparationTime(Integer minutes) {
        return minutes != null ? new PreparationTime(minutes) : null;
    }

    public String toString(ImageUrl url) {
        return url != null ? url.url() : null;
    }

    public ImageUrl toImageUrl(String value) {
        return value != null ? new ImageUrl(value) : null;
    }

    public ValidityPeriod toValidityPeriod(LocalDate from, LocalDate to) {
        return new ValidityPeriod(from, to);
    }

    public LocalDate toValidFrom(ValidityPeriod period) {
        return period != null ? period.start() : null;
    }

    public LocalDate toValidTo(ValidityPeriod period) {
        return period != null ? period.end() : null;
    }
}
