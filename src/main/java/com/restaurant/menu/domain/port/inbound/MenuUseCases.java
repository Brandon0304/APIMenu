package com.restaurant.menu.domain.port.inbound;

import com.restaurant.menu.domain.model.*;
import com.restaurant.menu.domain.model.dto.*;
import java.util.List;

public interface MenuUseCases {
    MenuResult create(CreateMenuCommand command);
    MenuResult getById(MenuId id);
    List<MenuResult> getAll(boolean activeOnly);
    MenuResult update(MenuId id, UpdateMenuCommand command);
    void delete(MenuId id);

    MenuSectionResult addSection(MenuId menuId, CategoryId categoryId, int displayOrder);
    void removeSection(MenuId menuId, MenuSectionId sectionId);
}
