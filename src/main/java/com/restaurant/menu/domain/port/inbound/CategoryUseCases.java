package com.restaurant.menu.domain.port.inbound;

import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.model.dto.CategoryResult;
import com.restaurant.menu.domain.model.dto.CreateCategoryCommand;
import com.restaurant.menu.domain.model.dto.UpdateCategoryCommand;
import java.util.List;

public interface CategoryUseCases {
    CategoryResult create(CreateCategoryCommand command);
    CategoryResult getById(CategoryId id);
    List<CategoryResult> getAll();
    CategoryResult update(CategoryId id, UpdateCategoryCommand command);
    CategoryResult updateStatus(CategoryId id, boolean active);
    void delete(CategoryId id);
}
