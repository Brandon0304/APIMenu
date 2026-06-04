package com.restaurant.menu.domain.model;

import java.util.Objects;

public class Ingredient {

    private final IngredientId id;
    private String name;
    private String description;
    private boolean active;
    private String unit;

    public Ingredient(IngredientId id, String name, String description, boolean active, String unit) {
        this.id = Objects.requireNonNull(id, "Ingredient id must not be null");
        setName(name);
        this.description = description;
        this.active = active;
        this.unit = unit;
    }

    public IngredientId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public String getUnit() {
        return unit;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name must not be blank");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public static Ingredient create(String name, String description, String unit) {
        return new Ingredient(IngredientId.generate(), name, description, true, unit);
    }
}
