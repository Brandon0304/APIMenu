package com.restaurant.menu.domain.model;

import java.util.Objects;

public class Category {

    private final CategoryId id;
    private String name;
    private String description;
    private int displayOrder;
    private boolean active;

    public Category(CategoryId id, String name, String description, int displayOrder, boolean active) {
        this.id = Objects.requireNonNull(id, "Category id must not be null");
        setName(name);
        setDisplayOrder(displayOrder);
        this.description = description;
        this.active = active;
    }

    public CategoryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name must not be blank");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisplayOrder(int displayOrder) {
        if (displayOrder < 0) {
            throw new IllegalArgumentException("Display order must not be negative, got: " + displayOrder);
        }
        this.displayOrder = displayOrder;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public static Category create(String name, String description, int displayOrder) {
        return new Category(CategoryId.generate(), name, description, displayOrder, true);
    }
}
