package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.*;
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
class ModifierGroupPersistenceAdapterTest {

    @Autowired
    private ModifierGroupPersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("TRUNCATE TABLE menu.menu_item_modifier_groups, menu.modifier_options, menu.modifier_groups CASCADE").executeUpdate();
    }

    @Test
    void shouldSaveAndFindModifierGroup() {
        ModifierGroup group = ModifierGroup.create("Size", "Choose size", false, 1);
        group.addOption(ModifierOption.create("Large", new Price(new java.math.BigDecimal("3.00"))));
        group.addOption(ModifierOption.create("Medium", new Price(new java.math.BigDecimal("1.50"))));

        ModifierGroup saved = adapter.save(group);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOptions()).hasSize(2);

        Optional<ModifierGroup> found = adapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getOptions()).hasSize(2);
    }
}
