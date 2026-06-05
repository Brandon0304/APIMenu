package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "menu_item_modifier_groups", schema = "menu")
@IdClass(MenuItemModifierGroupJpaEntity.MenuItemModifierGroupId.class)
public class MenuItemModifierGroupJpaEntity {

    @Id
    private UUID menuItemId;

    @Id
    private UUID modifierGroupId;

    public MenuItemModifierGroupJpaEntity() {}

    public MenuItemModifierGroupJpaEntity(UUID menuItemId, UUID modifierGroupId) {
        this.menuItemId = menuItemId;
        this.modifierGroupId = modifierGroupId;
    }

    public UUID getMenuItemId() { return menuItemId; }
    public void setMenuItemId(UUID menuItemId) { this.menuItemId = menuItemId; }
    public UUID getModifierGroupId() { return modifierGroupId; }
    public void setModifierGroupId(UUID modifierGroupId) { this.modifierGroupId = modifierGroupId; }

    public static class MenuItemModifierGroupId implements Serializable {
        private UUID menuItemId;
        private UUID modifierGroupId;

        public MenuItemModifierGroupId() {}

        public MenuItemModifierGroupId(UUID menuItemId, UUID modifierGroupId) {
            this.menuItemId = menuItemId;
            this.modifierGroupId = modifierGroupId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MenuItemModifierGroupId that)) return false;
            return Objects.equals(menuItemId, that.menuItemId) && Objects.equals(modifierGroupId, that.modifierGroupId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(menuItemId, modifierGroupId);
        }
    }
}
