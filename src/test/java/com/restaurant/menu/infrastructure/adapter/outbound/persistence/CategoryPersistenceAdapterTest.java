package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.Category;
import com.restaurant.menu.domain.model.CategoryId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategoryPersistenceAdapterTest {

    @Autowired
    private CategoryPersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("TRUNCATE TABLE menu.menu_item_allergens, menu.menu_item_modifier_groups, menu.menu_item_ingredients, menu.nutritional_info, menu.menu_sections, menu.modifier_options, menu.modifier_groups, menu.menu_items, menu.categories, menu.ingredients, menu.allergens CASCADE").executeUpdate();
    }

    @Test
    void shouldSaveAndFindCategory() {
        Category category = Category.create("Entradas", "Appetizers", 1);
        Category saved = adapter.save(category);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId().value()).isNotNull();

        Optional<Category> found = adapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Entradas");
        assertThat(found.get().getDescription()).isEqualTo("Appetizers");
    }

    @Test
    void shouldFindAllCategories() {
        adapter.save(Category.create("Entradas", null, 1));
        adapter.save(Category.create("Platos Fuertes", null, 2));
        assertThat(adapter.findAll()).hasSize(2);
    }

    @Test
    void shouldDeleteCategory() {
        Category category = adapter.save(Category.create("Test", null, 1));
        adapter.deleteById(category.getId());
        assertThat(adapter.findById(category.getId())).isEmpty();
    }

    @Test
    void shouldCheckNameExistence() {
        adapter.save(Category.create("Entradas", null, 1));
        assertThat(adapter.existsByName("Entradas")).isTrue();
        assertThat(adapter.existsByName("NonExistent")).isFalse();
    }
}
