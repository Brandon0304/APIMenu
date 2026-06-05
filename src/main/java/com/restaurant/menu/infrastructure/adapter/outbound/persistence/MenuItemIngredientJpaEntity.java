package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "menu_item_ingredients", schema = "menu")
@IdClass(MenuItemIngredientJpaEntity.MenuItemIngredientId.class)
public class MenuItemIngredientJpaEntity {

    @Id
    @Column(name = "menu_item_id")
    private UUID menuItemId;

    @Id
    @Column(name = "ingredient_id")
    private UUID ingredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false)
    private MenuItemJpaEntity menuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", insertable = false, updatable = false)
    private IngredientJpaEntity ingredient;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    public MenuItemIngredientJpaEntity() {}

    public UUID getMenuItemId() { return menuItemId; }
    public void setMenuItemId(UUID menuItemId) { this.menuItemId = menuItemId; }
    public UUID getIngredientId() { return ingredientId; }
    public void setIngredientId(UUID ingredientId) { this.ingredientId = ingredientId; }
    public MenuItemJpaEntity getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItemJpaEntity menuItem) { this.menuItem = menuItem; }
    public IngredientJpaEntity getIngredient() { return ingredient; }
    public void setIngredient(IngredientJpaEntity ingredient) { this.ingredient = ingredient; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public static class MenuItemIngredientId implements Serializable {
        private UUID menuItemId;
        private UUID ingredientId;

        public MenuItemIngredientId() {}

        public MenuItemIngredientId(UUID menuItemId, UUID ingredientId) {
            this.menuItemId = menuItemId;
            this.ingredientId = ingredientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MenuItemIngredientId that)) return false;
            return Objects.equals(menuItemId, that.menuItemId) && Objects.equals(ingredientId, that.ingredientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(menuItemId, ingredientId);
        }
    }
}
