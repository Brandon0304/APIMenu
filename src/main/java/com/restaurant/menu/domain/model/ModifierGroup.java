package com.restaurant.menu.domain.model;

import com.restaurant.menu.domain.exception.MaxSelectionsExceededException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ModifierGroup {

    private final ModifierGroupId id;
    private String name;
    private String description;
    private boolean required;
    private int maxSelections;
    private boolean active;
    private final List<ModifierOption> options;

    public ModifierGroup(ModifierGroupId id, String name, String description,
                         boolean required, int maxSelections, boolean active,
                         List<ModifierOption> options) {
        this.id = Objects.requireNonNull(id, "ModifierGroup id must not be null");
        setName(name);
        this.description = description;
        this.required = required;
        this.options = new ArrayList<>(options != null ? options : Collections.emptyList());
        setMaxSelections(maxSelections);
        this.active = active;
        validateRequired();
    }

    public ModifierGroupId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public int getMaxSelections() {
        return maxSelections;
    }

    public boolean isActive() {
        return active;
    }

    public List<ModifierOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("ModifierGroup name must not be blank");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequired(boolean required) {
        this.required = required;
        validateRequired();
    }

    public void setMaxSelections(int maxSelections) {
        if (maxSelections < 1) {
            throw new IllegalArgumentException("Max selections must be at least 1, got: " + maxSelections);
        }
        this.maxSelections = maxSelections;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void addOption(ModifierOption option) {
        Objects.requireNonNull(option, "Option must not be null");
        options.add(option);
    }

    public void removeOption(ModifierOptionId optionId) {
        options.removeIf(opt -> opt.getId().equals(optionId));
    }

    public Price calculatePriceImpact() {
        BigDecimal total = options.stream()
            .filter(ModifierOption::isActive)
            .map(opt -> opt.getPriceAdjustment().value())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Price(total);
    }

    private void validateRequired() {
        if (required && options.isEmpty()) {
            throw new IllegalStateException("Required modifier group must have at least one option");
        }
    }

    public static ModifierGroup create(String name, String description, boolean required, int maxSelections) {
        return new ModifierGroup(ModifierGroupId.generate(), name, description, required, maxSelections, true, new ArrayList<>());
    }
}
