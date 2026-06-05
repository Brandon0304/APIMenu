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
class MenuItemPersistenceAdapterTest {

    @Autowired
    private CategoryPersistenceAdapter categoryAdapter;

    @Autowired
    private MenuItemPersistenceAdapter adapter;

    @Autowired
    private IngredientPersistenceAdapter ingredientAdapter;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("TRUNCATE TABLE menu.menu_item_allergens, menu.menu_item_modifier_groups, menu.menu_item_ingredients, menu.nutritional_info, menu.menu_sections, menu.modifier_options, menu.modifier_groups, menu.menu_items, menu.categories, menu.ingredients, menu.allergens CASCADE").executeUpdate();
    }

    @Test
    void shouldSaveAndFindMenuItem() {
        Category category = categoryAdapter.save(Category.create("Bebidas", "Drinks", 1));
        assertThat(category.getId()).isNotNull();

        MenuItem item = MenuItem.create(
            "Coca Cola", "Refresco",
            new Price(new java.math.BigDecimal("2.50")),
            category.getId(),
            new PreparationTime(1),
            new ImageUrl("https://example.com/cola.jpg")
        );
        MenuItem saved = adapter.save(item);
        assertThat(saved.getId()).isNotNull();

        var found = adapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Coca Cola");
    }

    @Test
    void shouldSaveWithIngredients() {
        Category category = categoryAdapter.save(Category.create("Pizzas", null, 1));
        assertThat(category.getId()).isNotNull();

        Ingredient tomato = ingredientAdapter.save(Ingredient.create("Tomato", null, "grams"));
        Ingredient cheese = ingredientAdapter.save(Ingredient.create("Cheese", null, "grams"));

        MenuItem item = MenuItem.create(
            "Pizza Margherita", "Classic",
            new Price(new java.math.BigDecimal("10.00")),
            category.getId(),
            new PreparationTime(20), null
        );
        item.addIngredient(tomato, 100.0);
        item.addIngredient(cheese, 50.0);

        MenuItem saved = adapter.save(item);
        assertThat(saved.getIngredients()).hasSize(2);
    }

    @Test
    void shouldCheckNameExistence() {
        Category category = categoryAdapter.save(Category.create("TestCat", null, 1));
        adapter.save(MenuItem.create("Item1", null,
            new Price(new java.math.BigDecimal("5.00")),
            category.getId(), null, null));

        assertThat(adapter.existsByNameAndCategory("Item1", category.getId())).isTrue();
        assertThat(adapter.existsByNameAndCategory("NonExistent", category.getId())).isFalse();
    }
}
