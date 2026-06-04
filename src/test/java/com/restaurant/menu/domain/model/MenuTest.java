package com.restaurant.menu.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class MenuTest {

    @Test
    void shouldCreateMenu() {
        ValidityPeriod period = new ValidityPeriod(
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)
        );
        Menu menu = Menu.create("Summer Menu", "Seasonal offerings", period);
        assertThat(menu.getId()).isNotNull();
        assertThat(menu.getName()).isEqualTo("Summer Menu");
    }

    @Test
    void shouldAddSection() {
        Menu menu = createBasicMenu();
        CategoryId catId = CategoryId.generate();
        menu.addSection(MenuSection.create(catId, 1));
        assertThat(menu.getSections()).hasSize(1);
    }

    @Test
    void shouldRejectDuplicateCategoryInSections() {
        Menu menu = createBasicMenu();
        CategoryId catId = CategoryId.generate();
        menu.addSection(MenuSection.create(catId, 1));
        assertThatThrownBy(() -> menu.addSection(MenuSection.create(catId, 2)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRemoveSection() {
        Menu menu = createBasicMenu();
        MenuSection section = MenuSection.create(CategoryId.generate(), 1);
        menu.addSection(section);
        menu.removeSection(section.getId());
        assertThat(menu.getSections()).isEmpty();
    }

    @Test
    void shouldBeActiveWhenWithinValidityPeriod() {
        ValidityPeriod period = new ValidityPeriod(
            LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)
        );
        Menu menu = new Menu(MenuId.generate(), "Test", null, true, period, new ArrayList<>());
        assertThat(menu.isActive()).isTrue();
    }

    @Test
    void shouldBeInactiveWhenOutsideValidityPeriod() {
        ValidityPeriod period = new ValidityPeriod(
            LocalDate.now().minusDays(10), LocalDate.now().minusDays(5)
        );
        Menu menu = new Menu(MenuId.generate(), "Test", null, true, period, new ArrayList<>());
        assertThat(menu.isActive()).isFalse();
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> Menu.create("", null, null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private Menu createBasicMenu() {
        ValidityPeriod period = new ValidityPeriod(
            LocalDate.now().minusDays(1), LocalDate.now().plusDays(30)
        );
        return Menu.create("Test Menu", "Description", period);
    }
}
