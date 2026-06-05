package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuSectionJpaRepository extends JpaRepository<MenuSectionJpaEntity, UUID> {
    void deleteByMenuId(UUID menuId);
}
