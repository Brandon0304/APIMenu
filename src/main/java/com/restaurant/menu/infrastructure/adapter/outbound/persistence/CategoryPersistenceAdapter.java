package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.Category;
import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.port.outbound.CategoryRepositoryPort;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper.CategoryPersistenceMapper;
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
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository repository;
    private final CategoryPersistenceMapper mapper;

    public CategoryPersistenceAdapter(CategoryJpaRepository repository, CategoryPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id.value().toString()")
    public Optional<Category> findById(CategoryId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'all'")
    public List<Category> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Category save(Category category) {
        CategoryJpaEntity entity = mapper.toJpaEntity(category);
        boolean isNew = entity.getId() == null || !repository.existsById(entity.getId());
        if (isNew) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        CategoryJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteById(CategoryId id) {
        repository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
