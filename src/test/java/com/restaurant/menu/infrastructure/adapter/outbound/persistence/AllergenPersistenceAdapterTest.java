package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.AllergenId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AllergenPersistenceAdapterTest {

    @Autowired
    private AllergenPersistenceAdapter adapter;

    @Autowired
    private AllergenJpaRepository jpaRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("TRUNCATE TABLE menu.menu_item_allergens, menu.allergens CASCADE").executeUpdate();
    }

    @Test
    void shouldFindAllAllergens() {
        AllergenJpaEntity entity = new AllergenJpaEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setName("Test Allergen");
        entity.setActive(true);
        jpaRepository.save(entity);

        assertThat(adapter.findAll()).hasSize(1);
    }

    @Test
    void shouldFindAllergenById() {
        AllergenJpaEntity entity = new AllergenJpaEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setName("Gluten");
        entity.setActive(true);
        jpaRepository.save(entity);

        var found = adapter.findById(new AllergenId(entity.getId()));
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Gluten");
    }
}
