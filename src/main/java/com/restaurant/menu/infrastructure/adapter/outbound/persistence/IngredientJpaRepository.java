package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientJpaRepository extends JpaRepository<IngredientJpaEntity, UUID> {
    boolean existsByName(String name);
    List<IngredientJpaEntity> findByActiveTrue();
}
