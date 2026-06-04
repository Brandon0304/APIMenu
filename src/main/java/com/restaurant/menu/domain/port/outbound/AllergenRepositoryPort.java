package com.restaurant.menu.domain.port.outbound;

import com.restaurant.menu.domain.model.*;
import java.util.List;
import java.util.Optional;

public interface AllergenRepositoryPort {
    Optional<Allergen> findById(AllergenId id);
    List<Allergen> findAll();
}
