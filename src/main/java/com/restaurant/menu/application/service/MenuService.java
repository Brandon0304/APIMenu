package com.restaurant.menu.application.service;

import com.restaurant.menu.domain.exception.MenuNotFoundException;
import com.restaurant.menu.domain.model.Menu;
import com.restaurant.menu.domain.model.MenuId;
import com.restaurant.menu.domain.model.MenuSection;
import com.restaurant.menu.domain.model.MenuSectionId;
import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.model.dto.*;
import com.restaurant.menu.domain.port.inbound.MenuUseCases;
import com.restaurant.menu.domain.port.outbound.MenuRepositoryPort;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuService implements MenuUseCases {

    private final MenuRepositoryPort repository;

    public MenuService(MenuRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public MenuResult create(CreateMenuCommand command) {
        Menu menu = Menu.create(command.name(), command.description(), command.validityPeriod());
        Menu saved = repository.save(menu);
        return toResult(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResult getById(MenuId id) {
        return repository.findById(id)
            .map(MenuService::toResult)
            .orElseThrow(() -> new MenuNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResult> getAll(boolean activeOnly) {
        if (activeOnly) {
            return repository.findAll(true).stream()
                .map(MenuService::toResult)
                .toList();
        }
        return repository.findAll(false).stream()
            .map(MenuService::toResult)
            .toList();
    }

    @Override
    public MenuResult update(MenuId id, UpdateMenuCommand command) {
        Menu menu = repository.findById(id)
            .orElseThrow(() -> new MenuNotFoundException(id));
        menu.setName(command.name());
        menu.setDescription(command.description());
        menu.setValidityPeriod(command.validityPeriod());
        Menu saved = repository.save(menu);
        return toResult(saved);
    }

    @Override
    public void delete(MenuId id) {
        if (!repository.findById(id).isPresent()) {
            throw new MenuNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    public MenuSectionResult addSection(MenuId menuId, CategoryId categoryId, int displayOrder) {
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new MenuNotFoundException(menuId));
        MenuSection section = MenuSection.create(categoryId, displayOrder);
        menu.addSection(section);
        repository.save(menu);
        return new MenuSectionResult(section.getId(), section.getCategoryId(), section.getDisplayOrder());
    }

    @Override
    public void removeSection(MenuId menuId, MenuSectionId sectionId) {
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new MenuNotFoundException(menuId));
        menu.removeSection(sectionId);
        repository.save(menu);
    }

    static MenuResult toResult(Menu menu) {
        return new MenuResult(
            menu.getId(),
            menu.getName(),
            menu.getDescription(),
            menu.isActive(),
            menu.getValidityPeriod(),
            menu.getSections().stream()
                .map(s -> new MenuSectionResult(s.getId(), s.getCategoryId(), s.getDisplayOrder()))
                .toList(),
            Instant.now(),
            Instant.now()
        );
    }
}
