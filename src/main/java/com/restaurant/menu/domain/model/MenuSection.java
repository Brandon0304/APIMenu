package com.restaurant.menu.domain.model;

import java.util.Objects;

public class MenuSection {

    private final MenuSectionId id;
    private final CategoryId categoryId;
    private int displayOrder;

    public MenuSection(MenuSectionId id, CategoryId categoryId, int displayOrder) {
        this.id = Objects.requireNonNull(id, "MenuSection id must not be null");
        this.categoryId = Objects.requireNonNull(categoryId, "CategoryId must not be null");
        setDisplayOrder(displayOrder);
    }

    public MenuSectionId getId() {
        return id;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        if (displayOrder < 0) {
            throw new IllegalArgumentException("Display order must not be negative, got: " + displayOrder);
        }
        this.displayOrder = displayOrder;
    }

    public static MenuSection create(CategoryId categoryId, int displayOrder) {
        return new MenuSection(MenuSectionId.generate(), categoryId, displayOrder);
    }
}
