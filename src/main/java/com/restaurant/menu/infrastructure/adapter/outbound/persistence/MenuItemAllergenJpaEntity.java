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
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "menu_item_allergens", schema = "menu")
@IdClass(MenuItemAllergenJpaEntity.MenuItemAllergenId.class)
public class MenuItemAllergenJpaEntity {

    @Id
    @Column(name = "menu_item_id")
    private UUID menuItemId;

    @Id
    @Column(name = "allergen_id")
    private UUID allergenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false)
    private MenuItemJpaEntity menuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergen_id", insertable = false, updatable = false)
    private AllergenJpaEntity allergen;

    public MenuItemAllergenJpaEntity() {}

    public MenuItemAllergenJpaEntity(UUID menuItemId, UUID allergenId) {
        this.menuItemId = menuItemId;
        this.allergenId = allergenId;
    }

    public UUID getMenuItemId() { return menuItemId; }
    public void setMenuItemId(UUID menuItemId) { this.menuItemId = menuItemId; }
    public UUID getAllergenId() { return allergenId; }
    public void setAllergenId(UUID allergenId) { this.allergenId = allergenId; }
    public MenuItemJpaEntity getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItemJpaEntity menuItem) { this.menuItem = menuItem; }
    public AllergenJpaEntity getAllergen() { return allergen; }
    public void setAllergen(AllergenJpaEntity allergen) { this.allergen = allergen; }

    public static class MenuItemAllergenId implements Serializable {
        private UUID menuItemId;
        private UUID allergenId;

        public MenuItemAllergenId() {}

        public MenuItemAllergenId(UUID menuItemId, UUID allergenId) {
            this.menuItemId = menuItemId;
            this.allergenId = allergenId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MenuItemAllergenId that)) return false;
            return Objects.equals(menuItemId, that.menuItemId) && Objects.equals(allergenId, that.allergenId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(menuItemId, allergenId);
        }
    }
}
