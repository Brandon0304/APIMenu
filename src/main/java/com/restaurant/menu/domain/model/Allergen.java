package com.restaurant.menu.domain.model;

import java.util.Objects;

public class Allergen {

    private final AllergenId id;
    private String name;
    private String description;
    private String icon;

    public Allergen(AllergenId id, String name, String description, String icon) {
        this.id = Objects.requireNonNull(id, "Allergen id must not be null");
        setName(name);
        this.description = description;
        this.icon = icon;
    }

    public AllergenId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Allergen name must not be blank");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public static Allergen create(String name, String description, String icon) {
        return new Allergen(AllergenId.generate(), name, description, icon);
    }
}
