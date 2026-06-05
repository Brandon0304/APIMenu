package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.Menu;
import com.restaurant.menu.domain.model.MenuId;
import com.restaurant.menu.domain.port.outbound.MenuRepositoryPort;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper.MenuPersistenceMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class MenuPersistenceAdapter implements MenuRepositoryPort {

    private final MenuJpaRepository repository;
    private final MenuPersistenceMapper mapper;
    private final MenuSectionJpaRepository sectionRepository;

    public MenuPersistenceAdapter(MenuJpaRepository repository, MenuPersistenceMapper mapper, MenuSectionJpaRepository sectionRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.sectionRepository = sectionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menus", key = "#id.value().toString()")
    public Optional<Menu> findById(MenuId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menus", key = "'all-' + #activeOnly")
    public List<Menu> findAll(boolean activeOnly) {
        if (activeOnly) {
            return repository.findAll().stream()
                .filter(MenuJpaEntity::isActive)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        }
        return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "menus", allEntries = true),
        @CacheEvict(value = "menuItems", allEntries = true)
    })
    public Menu save(Menu menu) {
        MenuJpaEntity entity = mapper.toJpaEntity(menu);
        boolean isNew = entity.getId() == null || !repository.existsById(entity.getId());
        if (isNew) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());

        MenuJpaEntity saved = repository.save(entity);

        sectionRepository.deleteByMenuId(saved.getId());
        if (menu.getSections() != null) {
            for (var section : mapper.mapSections(menu.getSections())) {
                section.setMenu(saved);
                sectionRepository.save(section);
            }
        }

        return findById(new MenuId(saved.getId())).orElseThrow();
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "menus", allEntries = true),
        @CacheEvict(value = "menuItems", allEntries = true)
    })
    public void deleteById(MenuId id) {
        sectionRepository.deleteByMenuId(id.value());
        repository.deleteById(id.value());
    }
}
