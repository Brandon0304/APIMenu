package com.restaurant.menu.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MenuItem {

    private final MenuItemId id;
    private String name;
    private String description;
    private Price price;
    private CategoryId categoryId;
    private PreparationTime preparationTime;
    private ImageUrl imageUrl;
    private boolean active;
    private final List<IngredientQuantity> ingredients;
    private final List<ModifierGroup> modifierGroups;
    private final List<Allergen> allergens;
    private NutritionalInfo nutritionalInfo;

    public MenuItem(MenuItemId id, String name, String description, Price price,
                    CategoryId categoryId, PreparationTime preparationTime,
                    ImageUrl imageUrl, boolean active,
                    List<IngredientQuantity> ingredients,
                    List<ModifierGroup> modifierGroups,
                    List<Allergen> allergens,
                    NutritionalInfo nutritionalInfo) {
        this.id = Objects.requireNonNull(id, "MenuItem id must not be null");
        setName(name);
        this.description = description;
        setPrice(price);
        setCategoryId(categoryId);
        this.preparationTime = preparationTime;
        this.imageUrl = imageUrl;
        this.active = active;
        this.ingredients = new ArrayList<>(ingredients != null ? ingredients : Collections.emptyList());
        this.modifierGroups = new ArrayList<>(modifierGroups != null ? modifierGroups : Collections.emptyList());
        this.allergens = new ArrayList<>(allergens != null ? allergens : Collections.emptyList());
        this.nutritionalInfo = nutritionalInfo;
    }

    public MenuItemId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Price getPrice() {
        return price;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public PreparationTime getPreparationTime() {
        return preparationTime;
    }

    public ImageUrl getImageUrl() {
        return imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public List<IngredientQuantity> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public List<ModifierGroup> getModifierGroups() {
        return Collections.unmodifiableList(modifierGroups);
    }

    public List<Allergen> getAllergens() {
        return Collections.unmodifiableList(allergens);
    }

    public NutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("MenuItem name must not be blank");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Price price) {
        this.price = Objects.requireNonNull(price, "Price must not be null");
    }

    public void setCategoryId(CategoryId categoryId) {
        this.categoryId = Objects.requireNonNull(categoryId, "CategoryId must not be null");
    }

    public void setPreparationTime(PreparationTime preparationTime) {
        this.preparationTime = preparationTime;
    }

    public void setImageUrl(ImageUrl imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void addIngredient(Ingredient ingredient, double quantity) {
        Objects.requireNonNull(ingredient, "Ingredient must not be null");
        ingredients.stream()
            .filter(iq -> iq.getIngredient().getId().equals(ingredient.getId()))
            .findFirst()
            .ifPresentOrElse(
                iq -> iq.setQuantity(iq.getQuantity() + quantity),
                () -> ingredients.add(new IngredientQuantity(ingredient, quantity))
            );
    }

    public void removeIngredient(IngredientId ingredientId) {
        ingredients.removeIf(iq -> iq.getIngredient().getId().equals(ingredientId));
    }

    public void assignModifierGroup(ModifierGroup modifierGroup) {
        Objects.requireNonNull(modifierGroup, "ModifierGroup must not be null");
        boolean exists = modifierGroups.stream()
            .anyMatch(mg -> mg.getId().equals(modifierGroup.getId()));
        if (exists) {
            throw new IllegalArgumentException("ModifierGroup already assigned to this menu item");
        }
        modifierGroups.add(modifierGroup);
    }

    public void removeModifierGroup(ModifierGroupId modifierGroupId) {
        modifierGroups.removeIf(mg -> mg.getId().equals(modifierGroupId));
    }

    public void assignNutritionalInfo(NutritionalInfo nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    public void addAllergen(Allergen allergen) {
        Objects.requireNonNull(allergen, "Allergen must not be null");
        boolean exists = allergens.stream()
            .anyMatch(a -> a.getId().equals(allergen.getId()));
        if (!exists) {
            allergens.add(allergen);
        }
    }

    public void removeAllergen(AllergenId allergenId) {
        allergens.removeIf(a -> a.getId().equals(allergenId));
    }

    public Price calculateFinalPrice() {
        var totalMandatoryAdjustment = modifierGroups.stream()
            .filter(ModifierGroup::isRequired)
            .filter(ModifierGroup::isActive)
            .map(ModifierGroup::calculatePriceImpact)
            .map(Price::value)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return new Price(price.value().add(totalMandatoryAdjustment));
    }

    public static MenuItem create(String name, String description, Price price,
                                  CategoryId categoryId, PreparationTime preparationTime,
                                  ImageUrl imageUrl) {
        return new MenuItem(MenuItemId.generate(), name, description, price,
            categoryId, preparationTime, imageUrl, true,
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
    }
}
