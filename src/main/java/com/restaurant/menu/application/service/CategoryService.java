package com.restaurant.menu.application.service;

import com.restaurant.menu.domain.exception.CategoryNameDuplicatedException;
import com.restaurant.menu.domain.exception.CategoryNotFoundException;
import com.restaurant.menu.domain.model.Category;
import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.model.dto.CategoryResult;
import com.restaurant.menu.domain.model.dto.CreateCategoryCommand;
import com.restaurant.menu.domain.model.dto.UpdateCategoryCommand;
import com.restaurant.menu.domain.port.inbound.CategoryUseCases;
import com.restaurant.menu.domain.port.outbound.CategoryRepositoryPort;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService implements CategoryUseCases {

    private final CategoryRepositoryPort repository;

    public CategoryService(CategoryRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public CategoryResult create(CreateCategoryCommand command) {
        if (repository.existsByName(command.name())) {
            throw new CategoryNameDuplicatedException(command.name());
        }
        Category category = Category.create(command.name(), command.description(), command.displayOrder());
        Category saved = repository.save(category);
        return toResult(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResult getById(CategoryId id) {
        return repository.findById(id)
            .map(CategoryService::toResult)
            .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResult> getAll() {
        return repository.findAll().stream()
            .map(CategoryService::toResult)
            .toList();
    }

    @Override
    public CategoryResult update(CategoryId id, UpdateCategoryCommand command) {
        Category category = repository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));
        if (!category.getName().equals(command.name()) && repository.existsByName(command.name())) {
            throw new CategoryNameDuplicatedException(command.name());
        }
        category.setName(command.name());
        category.setDescription(command.description());
        category.setDisplayOrder(command.displayOrder());
        Category saved = repository.save(category);
        return toResult(saved);
    }

    @Override
    public CategoryResult updateStatus(CategoryId id, boolean active) {
        Category category = repository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));
        if (active) category.activate();
        else category.deactivate();
        Category saved = repository.save(category);
        return toResult(saved);
    }

    @Override
    public void delete(CategoryId id) {
        if (!repository.findById(id).isPresent()) {
            throw new CategoryNotFoundException(id);
        }
        repository.deleteById(id);
    }

    static CategoryResult toResult(Category category) {
        return new CategoryResult(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getDisplayOrder(),
            category.isActive(),
            Instant.now(),
            Instant.now()
        );
    }
}
