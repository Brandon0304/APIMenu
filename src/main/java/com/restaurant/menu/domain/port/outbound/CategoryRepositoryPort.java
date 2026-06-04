package com.restaurant.menu.domain.port.outbound;

import com.restaurant.menu.domain.model.Category;
import com.restaurant.menu.domain.model.CategoryId;
import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Optional<Category> findById(CategoryId id);
    List<Category> findAll();
    Category save(Category category);
    void deleteById(CategoryId id);
    boolean existsByName(String name);
}
