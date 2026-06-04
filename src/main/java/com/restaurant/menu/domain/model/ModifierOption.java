package com.restaurant.menu.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ModifierOption {

    private final ModifierOptionId id;
    private String name;
    private Price priceAdjustment;
    private boolean active;

    public ModifierOption(ModifierOptionId id, String name, Price priceAdjustment, boolean active) {
        this.id = Objects.requireNonNull(id, "ModifierOption id must not be null");
        setName(name);
        this.priceAdjustment = priceAdjustment;
        this.active = active;
    }

    public ModifierOptionId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Price getPriceAdjustment() {
        return priceAdjustment;
    }

    public boolean isActive() {
        return active;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("ModifierOption name must not be blank");
        }
        this.name = name.trim();
    }

    public void setPriceAdjustment(Price priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public static ModifierOption create(String name, Price priceAdjustment) {
        return new ModifierOption(ModifierOptionId.generate(), name, priceAdjustment, true);
    }
}
