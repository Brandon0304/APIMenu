package com.restaurant.menu.application.service;

import com.restaurant.menu.domain.exception.CategoryNotFoundException;
import com.restaurant.menu.domain.exception.MenuItemNameDuplicatedException;
import com.restaurant.menu.domain.exception.MenuItemNotFoundException;
import com.restaurant.menu.domain.model.*;
import com.restaurant.menu.domain.model.dto.*;
import com.restaurant.menu.domain.port.inbound.MenuItemUseCases;
import com.restaurant.menu.domain.port.outbound.AllergenRepositoryPort;
import com.restaurant.menu.domain.port.outbound.CategoryRepositoryPort;
import com.restaurant.menu.domain.port.outbound.IngredientRepositoryPort;
import com.restaurant.menu.domain.port.outbound.MenuItemRepositoryPort;
import com.restaurant.menu.domain.port.outbound.ModifierGroupRepositoryPort;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuItemService implements MenuItemUseCases {

    private final MenuItemRepositoryPort repository;
    private final CategoryRepositoryPort categoryRepository;
    private final IngredientRepositoryPort ingredientRepository;
    private final AllergenRepositoryPort allergenRepository;
    private final ModifierGroupRepositoryPort modifierGroupRepository;

    public MenuItemService(MenuItemRepositoryPort repository,
                           CategoryRepositoryPort categoryRepository,
                           IngredientRepositoryPort ingredientRepository,
                           AllergenRepositoryPort allergenRepository,
                           ModifierGroupRepositoryPort modifierGroupRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.allergenRepository = allergenRepository;
        this.modifierGroupRepository = modifierGroupRepository;
    }

    @Override
    public MenuItemResult create(CreateMenuItemCommand command) {
        if (repository.existsByNameAndCategory(command.name(), command.categoryId())) {
            throw new MenuItemNameDuplicatedException(command.name(), command.categoryId());
        }
        if (!categoryRepository.findById(command.categoryId()).isPresent()) {
            throw new CategoryNotFoundException(command.categoryId());
        }
        MenuItem item = MenuItem.create(
            command.name(), command.description(), command.price(),
            command.categoryId(), command.preparationTime(), command.imageUrl());
        MenuItem saved = repository.save(item);
        return toResult(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResult getById(MenuItemId id) {
        return repository.findById(id)
            .map(MenuItemService::toResult)
            .orElseThrow(() -> new MenuItemNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResult> getAll(int page, int size) {
        return repository.findAll(page, size).stream()
            .map(MenuItemService::toResult)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResult> getByCategoryId(CategoryId categoryId) {
        return repository.findByCategoryId(categoryId).stream()
            .map(MenuItemService::toResult)
            .toList();
    }

    @Override
    public MenuItemResult update(MenuItemId id, UpdateMenuItemCommand command) {
        MenuItem item = repository.findById(id)
            .orElseThrow(() -> new MenuItemNotFoundException(id));
        if (!item.getName().equals(command.name())
            && repository.existsByNameAndCategory(command.name(), command.categoryId())) {
            throw new MenuItemNameDuplicatedException(command.name(), command.categoryId());
        }
        item.setName(command.name());
        item.setDescription(command.description());
        item.setPrice(command.price());
        item.setCategoryId(command.categoryId());
        item.setPreparationTime(command.preparationTime());
        item.setImageUrl(command.imageUrl());
        MenuItem saved = repository.save(item);
        return toResult(saved);
    }

    @Override
    public MenuItemResult updateStatus(MenuItemId id, boolean active) {
        MenuItem item = repository.findById(id)
            .orElseThrow(() -> new MenuItemNotFoundException(id));
        if (active) item.activate();
        else item.deactivate();
        MenuItem saved = repository.save(item);
        return toResult(saved);
    }

    @Override
    public void delete(MenuItemId id) {
        if (!repository.findById(id).isPresent()) {
            throw new MenuItemNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    public void addIngredient(MenuItemId menuItemId, IngredientId ingredientId, double quantity) {
        MenuItem item = repository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
            .orElseThrow(() -> new com.restaurant.menu.domain.exception.IngredientNotFoundException(ingredientId));
        item.addIngredient(ingredient, quantity);
        repository.save(item);
    }

    @Override
    public void removeIngredient(MenuItemId menuItemId, IngredientId ingredientId) {
        MenuItem item = repository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        item.removeIngredient(ingredientId);
        repository.save(item);
    }

    @Override
    public void assignModifierGroup(MenuItemId menuItemId, ModifierGroupId modifierGroupId) {
        MenuItem item = repository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        ModifierGroup group = modifierGroupRepository.findById(modifierGroupId)
            .orElseThrow(() -> new com.restaurant.menu.domain.exception.ModifierGroupNotFoundException(modifierGroupId));
        item.assignModifierGroup(group);
        repository.save(item);
    }

    @Override
    public void removeModifierGroup(MenuItemId menuItemId, ModifierGroupId modifierGroupId) {
        MenuItem item = repository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        item.removeModifierGroup(modifierGroupId);
        repository.save(item);
    }

    @Override
    public void addAllergen(MenuItemId menuItemId, AllergenId allergenId) {
        MenuItem item = repository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        Allergen allergen = allergenRepository.findById(allergenId)
            .orElseThrow(() -> new com.restaurant.menu.domain.exception.AllergenNotFoundException(allergenId));
        item.addAllergen(allergen);
        repository.save(item);
    }

    @Override
    public void removeAllergen(MenuItemId menuItemId, AllergenId allergenId) {
        MenuItem item = repository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        item.removeAllergen(allergenId);
        repository.save(item);
    }

    @Override
    public NutritionalInfoResult updateNutritionalInfo(MenuItemId menuItemId, UpdateNutritionalInfoCommand command) {
        MenuItem item = repository.findById(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
        NutritionalInfo info = new NutritionalInfo(
            command.calories(), command.proteinGrams(), command.carbsGrams(),
            command.fatGrams(), command.fiberGrams(), command.sodiumMg());
        item.assignNutritionalInfo(info);
        repository.save(item);
        return new NutritionalInfoResult(
            info.calories(), info.proteinGrams(), info.carbsGrams(),
            info.fatGrams(), info.fiberGrams(), info.sodiumMg());
    }

    static MenuItemResult toResult(MenuItem item) {
        return new MenuItemResult(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getPrice(),
            item.getCategoryId(),
            item.isActive(),
            item.getPreparationTime(),
            item.getImageUrl(),
            Instant.now(),
            Instant.now()
        );
    }
}
