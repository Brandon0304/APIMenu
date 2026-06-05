package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionalInfoJpaRepository extends JpaRepository<NutritionalInfoJpaEntity, UUID> {
    void deleteByMenuItemId(UUID menuItemId);
}
