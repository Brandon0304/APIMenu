package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.Allergen;
import com.restaurant.menu.domain.model.AllergenId;
import com.restaurant.menu.domain.port.outbound.AllergenRepositoryPort;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper.AllergenPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AllergenPersistenceAdapter implements AllergenRepositoryPort {

    private final AllergenJpaRepository repository;
    private final AllergenPersistenceMapper mapper;

    public AllergenPersistenceAdapter(AllergenJpaRepository repository, AllergenPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allergens", key = "#id.value().toString()")
    public Optional<Allergen> findById(AllergenId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allergens", key = "'all'")
    public List<Allergen> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
