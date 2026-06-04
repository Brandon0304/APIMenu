package com.restaurant.menu.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Menu {

    private final MenuId id;
    private String name;
    private String description;
    private boolean active;
    private ValidityPeriod validityPeriod;
    private final List<MenuSection> sections;

    public Menu(MenuId id, String name, String description, boolean active,
                ValidityPeriod validityPeriod, List<MenuSection> sections) {
        this.id = Objects.requireNonNull(id, "Menu id must not be null");
        setName(name);
        this.description = description;
        this.active = active;
        this.validityPeriod = validityPeriod;
        this.sections = new ArrayList<>(sections != null ? sections : Collections.emptyList());
    }

    public MenuId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active && (validityPeriod == null || validityPeriod.isActive());
    }

    public ValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public List<MenuSection> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Menu name must not be blank");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void setValidityPeriod(ValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public void addSection(MenuSection section) {
        Objects.requireNonNull(section, "MenuSection must not be null");
        boolean exists = sections.stream()
            .anyMatch(s -> s.getCategoryId().equals(section.getCategoryId()));
        if (exists) {
            throw new IllegalArgumentException("Category already exists in this menu: " + section.getCategoryId().value());
        }
        sections.add(section);
    }

    public void removeSection(MenuSectionId sectionId) {
        sections.removeIf(s -> s.getId().equals(sectionId));
    }

    public static Menu create(String name, String description, ValidityPeriod validityPeriod) {
        return new Menu(MenuId.generate(), name, description, true, validityPeriod, new ArrayList<>());
    }
}
