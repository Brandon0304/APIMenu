package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.Ingredient;
import com.restaurant.menu.domain.model.IngredientId;
import com.restaurant.menu.domain.port.outbound.IngredientRepositoryPort;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper.IngredientPersistenceMapper;
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
public class IngredientPersistenceAdapter implements IngredientRepositoryPort {

    private final IngredientJpaRepository repository;
    private final IngredientPersistenceMapper mapper;

    public IngredientPersistenceAdapter(IngredientJpaRepository repository, IngredientPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ingredients", key = "#id.value().toString()")
    public Optional<Ingredient> findById(IngredientId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ingredients", key = "'all'")
    public List<Ingredient> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "ingredients", allEntries = true)
    public Ingredient save(Ingredient ingredient) {
        IngredientJpaEntity entity = mapper.toJpaEntity(ingredient);
        boolean isNew = entity.getId() == null || !repository.existsById(entity.getId());
        if (isNew) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        IngredientJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @CacheEvict(value = "ingredients", allEntries = true)
    public void deleteById(IngredientId id) {
        repository.deleteById(id.value());
    }
}
