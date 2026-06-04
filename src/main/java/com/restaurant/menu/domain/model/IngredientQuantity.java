package com.restaurant.menu.domain.model;

import java.util.Objects;

public class IngredientQuantity {

    private final Ingredient ingredient;
    private double quantity;

    public IngredientQuantity(Ingredient ingredient, double quantity) {
        this.ingredient = Objects.requireNonNull(ingredient, "Ingredient must not be null");
        setQuantity(quantity);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);
        }
        this.quantity = quantity;
    }
}
