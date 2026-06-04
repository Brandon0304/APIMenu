package com.restaurant.menu.domain.port.outbound;

import com.restaurant.menu.domain.model.*;
import java.util.List;
import java.util.Optional;

public interface IngredientRepositoryPort {
    Optional<Ingredient> findById(IngredientId id);
    List<Ingredient> findAll();
    Ingredient save(Ingredient ingredient);
    void deleteById(IngredientId id);
}
