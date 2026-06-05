package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemModifierGroupJpaRepository extends JpaRepository<MenuItemModifierGroupJpaEntity, MenuItemModifierGroupJpaEntity.MenuItemModifierGroupId> {
    void deleteByMenuItemId(UUID menuItemId);
}
