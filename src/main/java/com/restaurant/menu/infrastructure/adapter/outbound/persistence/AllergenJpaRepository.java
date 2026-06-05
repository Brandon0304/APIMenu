package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergenJpaRepository extends JpaRepository<AllergenJpaEntity, UUID> {
    boolean existsByName(String name);
}
