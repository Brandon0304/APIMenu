package com.restaurant.menu.domain.port.outbound;

import com.restaurant.menu.domain.model.*;
import java.util.List;
import java.util.Optional;

public interface MenuItemRepositoryPort {
    Optional<MenuItem> findById(MenuItemId id);
    List<MenuItem> findAll(int page, int size);
    List<MenuItem> findByCategoryId(CategoryId categoryId);
    MenuItem save(MenuItem menuItem);
    void deleteById(MenuItemId id);
    boolean existsByNameAndCategory(String name, CategoryId categoryId);
}
