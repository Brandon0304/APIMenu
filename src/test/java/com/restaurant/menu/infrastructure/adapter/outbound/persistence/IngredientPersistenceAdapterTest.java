package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.*;
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
class IngredientPersistenceAdapterTest {

    @Autowired
    private IngredientPersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("TRUNCATE TABLE menu.menu_item_ingredients, menu.ingredients CASCADE").executeUpdate();
    }

    @Test
    void shouldSaveAndFindIngredient() {
        Ingredient saved = adapter.save(Ingredient.create("Tomato", "Fresh red tomato", "grams"));
        assertThat(saved.getId()).isNotNull();

        var found = adapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Tomato");
    }
}
