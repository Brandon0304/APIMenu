package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.ModifierGroup;
import com.restaurant.menu.domain.model.ModifierGroupId;
import com.restaurant.menu.domain.port.outbound.ModifierGroupRepositoryPort;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper.ModifierGroupPersistenceMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ModifierGroupPersistenceAdapter implements ModifierGroupRepositoryPort {

    private final ModifierGroupJpaRepository repository;
    private final ModifierGroupPersistenceMapper mapper;

    public ModifierGroupPersistenceAdapter(ModifierGroupJpaRepository repository, ModifierGroupPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "modifierGroups", key = "#id.value().toString()")
    public Optional<ModifierGroup> findById(ModifierGroupId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "modifierGroups", key = "'all'")
    public List<ModifierGroup> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "modifierGroups", allEntries = true)
    public ModifierGroup save(ModifierGroup modifierGroup) {
        ModifierGroupJpaEntity entity = mapper.toJpaEntity(modifierGroup);
        boolean isNew = entity.getId() == null || !repository.existsById(entity.getId());
        if (isNew) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        for (ModifierOptionJpaEntity opt : entity.getOptions()) {
            if (opt.getCreatedAt() == null) {
                opt.setCreatedAt(LocalDateTime.now());
            }
            opt.setUpdatedAt(LocalDateTime.now());
            opt.setModifierGroup(entity);
        }
        ModifierGroupJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @CacheEvict(value = "modifierGroups", allEntries = true)
    public void deleteById(ModifierGroupId id) {
        repository.deleteById(id.value());
    }
}
