package com.restaurant.menu.application.service;

import com.restaurant.menu.domain.exception.IngredientNotFoundException;
import com.restaurant.menu.domain.model.Ingredient;
import com.restaurant.menu.domain.model.IngredientId;
import com.restaurant.menu.domain.model.dto.CreateIngredientCommand;
import com.restaurant.menu.domain.model.dto.IngredientResult;
import com.restaurant.menu.domain.model.dto.UpdateIngredientCommand;
import com.restaurant.menu.domain.port.inbound.IngredientUseCases;
import com.restaurant.menu.domain.port.outbound.IngredientRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IngredientService implements IngredientUseCases {

    private final IngredientRepositoryPort repository;

    public IngredientService(IngredientRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public IngredientResult create(CreateIngredientCommand command) {
        Ingredient ingredient = Ingredient.create(command.name(), command.description(), command.unit());
        Ingredient saved = repository.save(ingredient);
        return toResult(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientResult getById(IngredientId id) {
        return repository.findById(id)
            .map(IngredientService::toResult)
            .orElseThrow(() -> new IngredientNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientResult> getAll() {
        return repository.findAll().stream()
            .map(IngredientService::toResult)
            .toList();
    }

    @Override
    public IngredientResult update(IngredientId id, UpdateIngredientCommand command) {
        Ingredient ingredient = repository.findById(id)
            .orElseThrow(() -> new IngredientNotFoundException(id));
        ingredient.setName(command.name());
        ingredient.setDescription(command.description());
        ingredient.setUnit(command.unit());
        Ingredient saved = repository.save(ingredient);
        return toResult(saved);
    }

    @Override
    public void delete(IngredientId id) {
        if (!repository.findById(id).isPresent()) {
            throw new IngredientNotFoundException(id);
        }
        repository.deleteById(id);
    }

    static IngredientResult toResult(Ingredient ingredient) {
        return new IngredientResult(
            ingredient.getId(),
            ingredient.getName(),
            ingredient.getDescription(),
            ingredient.isActive(),
            ingredient.getUnit()
        );
    }
}
