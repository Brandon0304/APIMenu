package com.restaurant.menu.application.service;

import com.restaurant.menu.domain.exception.ModifierGroupNotFoundException;
import com.restaurant.menu.domain.model.ModifierGroup;
import com.restaurant.menu.domain.model.ModifierGroupId;
import com.restaurant.menu.domain.model.ModifierOption;
import com.restaurant.menu.domain.model.ModifierOptionId;
import com.restaurant.menu.domain.model.dto.CreateModifierGroupCommand;
import com.restaurant.menu.domain.model.dto.CreateModifierOptionCommand;
import com.restaurant.menu.domain.model.dto.ModifierGroupResult;
import com.restaurant.menu.domain.model.dto.ModifierOptionResult;
import com.restaurant.menu.domain.model.dto.UpdateModifierGroupCommand;
import com.restaurant.menu.domain.model.dto.UpdateModifierOptionCommand;
import com.restaurant.menu.domain.port.inbound.ModifierGroupUseCases;
import com.restaurant.menu.domain.port.outbound.ModifierGroupRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModifierGroupService implements ModifierGroupUseCases {

    private final ModifierGroupRepositoryPort repository;

    public ModifierGroupService(ModifierGroupRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public ModifierGroupResult create(CreateModifierGroupCommand command) {
        ModifierGroup group = ModifierGroup.create(
            command.name(), command.description(), command.required(), command.maxSelections());
        ModifierGroup saved = repository.save(group);
        return toResult(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ModifierGroupResult getById(ModifierGroupId id) {
        return repository.findById(id)
            .map(ModifierGroupService::toResult)
            .orElseThrow(() -> new ModifierGroupNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModifierGroupResult> getAll() {
        return repository.findAll().stream()
            .map(ModifierGroupService::toResult)
            .toList();
    }

    @Override
    public ModifierGroupResult update(ModifierGroupId id, UpdateModifierGroupCommand command) {
        ModifierGroup group = repository.findById(id)
            .orElseThrow(() -> new ModifierGroupNotFoundException(id));
        group.setName(command.name());
        group.setDescription(command.description());
        group.setRequired(command.required());
        group.setMaxSelections(command.maxSelections());
        ModifierGroup saved = repository.save(group);
        return toResult(saved);
    }

    @Override
    public void delete(ModifierGroupId id) {
        if (!repository.findById(id).isPresent()) {
            throw new ModifierGroupNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    public ModifierOptionResult addOption(ModifierGroupId groupId, CreateModifierOptionCommand command) {
        ModifierGroup group = repository.findById(groupId)
            .orElseThrow(() -> new ModifierGroupNotFoundException(groupId));
        ModifierOption option = ModifierOption.create(command.name(), command.priceAdjustment());
        group.addOption(option);
        repository.save(group);
        return toOptionResult(option);
    }

    @Override
    public ModifierOptionResult updateOption(ModifierGroupId groupId, ModifierOptionId optionId, UpdateModifierOptionCommand command) {
        ModifierGroup group = repository.findById(groupId)
            .orElseThrow(() -> new ModifierGroupNotFoundException(groupId));
        ModifierOption option = group.getOptions().stream()
            .filter(o -> o.getId().equals(optionId))
            .findFirst()
            .orElseThrow(() -> new ModifierGroupNotFoundException(groupId));
        option.setName(command.name());
        option.setPriceAdjustment(command.priceAdjustment());
        repository.save(group);
        return toOptionResult(option);
    }

    @Override
    public void removeOption(ModifierGroupId groupId, ModifierOptionId optionId) {
        ModifierGroup group = repository.findById(groupId)
            .orElseThrow(() -> new ModifierGroupNotFoundException(groupId));
        group.removeOption(optionId);
        repository.save(group);
    }

    static ModifierGroupResult toResult(ModifierGroup group) {
        return new ModifierGroupResult(
            group.getId(),
            group.getName(),
            group.getDescription(),
            group.isRequired(),
            group.getMaxSelections(),
            group.isActive(),
            group.getOptions().stream().map(ModifierGroupService::toOptionResult).toList()
        );
    }

    static ModifierOptionResult toOptionResult(ModifierOption option) {
        return new ModifierOptionResult(
            option.getId(),
            option.getName(),
            option.getPriceAdjustment(),
            option.isActive()
        );
    }
}
