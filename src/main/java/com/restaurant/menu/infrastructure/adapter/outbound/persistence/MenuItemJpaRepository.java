package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemJpaRepository extends JpaRepository<MenuItemJpaEntity, UUID> {
    List<MenuItemJpaEntity> findByCategoryId(UUID categoryId);

    boolean existsByNameAndCategoryId(String name, UUID categoryId);
}
