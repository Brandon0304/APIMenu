package com.restaurant.menu.domain.port.inbound;

import com.restaurant.menu.domain.model.*;
import com.restaurant.menu.domain.model.dto.*;
import java.util.List;

public interface IngredientUseCases {
    IngredientResult create(CreateIngredientCommand command);
    IngredientResult getById(IngredientId id);
    List<IngredientResult> getAll();
    IngredientResult update(IngredientId id, UpdateIngredientCommand command);
    void delete(IngredientId id);
}
