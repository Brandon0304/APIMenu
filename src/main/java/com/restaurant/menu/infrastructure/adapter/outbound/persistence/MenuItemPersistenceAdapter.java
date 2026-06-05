package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.model.MenuItem;
import com.restaurant.menu.domain.model.MenuItemId;
import com.restaurant.menu.domain.port.outbound.MenuItemRepositoryPort;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper.MenuItemPersistenceMapper;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper.ValueObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class MenuItemPersistenceAdapter implements MenuItemRepositoryPort {

    private final MenuItemJpaRepository repository;
    private final MenuItemIngredientJpaRepository ingredientJoinRepository;
    private final MenuItemAllergenJpaRepository allergenJoinRepository;
    private final MenuItemModifierGroupJpaRepository modifierGroupJoinRepository;
    private final NutritionalInfoJpaRepository nutritionalInfoRepository;
    private final MenuItemPersistenceMapper mapper;
    private final ValueObjectMapper valueObjectMapper;

    public MenuItemPersistenceAdapter(MenuItemJpaRepository repository,
                                      MenuItemIngredientJpaRepository ingredientJoinRepository,
                                      MenuItemAllergenJpaRepository allergenJoinRepository,
                                      MenuItemModifierGroupJpaRepository modifierGroupJoinRepository,
                                      NutritionalInfoJpaRepository nutritionalInfoRepository,
                                      MenuItemPersistenceMapper mapper,
                                      ValueObjectMapper valueObjectMapper) {
        this.repository = repository;
        this.ingredientJoinRepository = ingredientJoinRepository;
        this.allergenJoinRepository = allergenJoinRepository;
        this.modifierGroupJoinRepository = modifierGroupJoinRepository;
        this.nutritionalInfoRepository = nutritionalInfoRepository;
        this.mapper = mapper;
        this.valueObjectMapper = valueObjectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuItem> findById(MenuItemId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> findAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> findByCategoryId(CategoryId categoryId) {
        return repository.findByCategoryId(categoryId.value()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        MenuItemJpaEntity entity = mapper.toJpaEntity(menuItem);
        boolean isNew = entity.getId() == null || !repository.existsById(entity.getId());
        if (isNew) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());

        MenuItemJpaEntity saved = repository.save(entity);
        UUID savedId = saved.getId();

        ingredientJoinRepository.deleteByMenuItemId(savedId);
        List<MenuItemIngredientJpaEntity> ingredients = mapper.updateIngredientEntities(saved, menuItem);
        List<MenuItemIngredientJpaEntity> savedIngredients = ingredientJoinRepository.saveAll(ingredients);
        saved.setIngredients(savedIngredients);

        modifierGroupJoinRepository.deleteByMenuItemId(savedId);
        List<UUID> modifierGroupIds = mapper.mapModifierGroupIds(menuItem);
        for (UUID mgId : modifierGroupIds) {
            modifierGroupJoinRepository.save(new MenuItemModifierGroupJpaEntity(savedId, mgId));
        }

        allergenJoinRepository.deleteByMenuItemId(savedId);
        List<MenuItemAllergenJpaEntity> allergens = mapper.mapAllergens(menuItem, savedId);
        allergenJoinRepository.saveAll(allergens);

        nutritionalInfoRepository.deleteByMenuItemId(savedId);
        if (menuItem.getNutritionalInfo() != null) {
            NutritionalInfoJpaEntity nutritionalEntity = valueObjectMapper.toJpaEntity(menuItem.getNutritionalInfo());
            nutritionalEntity.setMenuItemId(savedId);
            nutritionalEntity.setCreatedAt(LocalDateTime.now());
            nutritionalEntity.setUpdatedAt(LocalDateTime.now());
            nutritionalInfoRepository.save(nutritionalEntity);
        }

        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(MenuItemId id) {
        UUID uuid = id.value();
        ingredientJoinRepository.deleteByMenuItemId(uuid);
        modifierGroupJoinRepository.deleteByMenuItemId(uuid);
        allergenJoinRepository.deleteByMenuItemId(uuid);
        nutritionalInfoRepository.deleteByMenuItemId(uuid);
        repository.deleteById(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndCategory(String name, CategoryId categoryId) {
        return repository.existsByNameAndCategoryId(name, categoryId.value());
    }
}
