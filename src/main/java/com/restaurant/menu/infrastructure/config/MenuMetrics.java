package com.restaurant.menu.infrastructure.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class MenuMetrics {

    private final MeterRegistry meterRegistry;

    public MenuMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordMenuItemCreated() {
        meterRegistry.counter("menu.items.created").increment();
    }

    public void recordMenuItemDeleted() {
        meterRegistry.counter("menu.items.deleted").increment();
    }

    public void recordCategoryCreated() {
        meterRegistry.counter("categories.created").increment();
    }

    public void recordCategoryDeleted() {
        meterRegistry.counter("categories.deleted").increment();
    }

    public void recordMenuCreated() {
        meterRegistry.counter("menus.created").increment();
    }

    public void recordMenuActivated() {
        meterRegistry.counter("menus.activated").increment();
    }

    public void recordIngredientCreated() {
        meterRegistry.counter("ingredients.created").increment();
    }

    public void recordModifierGroupCreated() {
        meterRegistry.counter("modifier.groups.created").increment();
    }

    public void recordPriceCalculationTime(long millis) {
        Timer.builder("menu.price.calculation.time")
            .description("Time taken to calculate menu item prices")
            .register(meterRegistry)
            .record(Duration.ofMillis(millis));
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample, String operation) {
        sample.stop(Timer.builder("menu.operation.duration")
            .tag("operation", operation)
            .register(meterRegistry));
    }
}
