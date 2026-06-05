package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemIngredientJpaRepository extends JpaRepository<MenuItemIngredientJpaEntity, MenuItemIngredientJpaEntity.MenuItemIngredientId> {
    void deleteByMenuItemId(UUID menuItemId);
}
