package com.restaurant.menu.domain.port.outbound;

import com.restaurant.menu.domain.model.*;
import java.util.List;
import java.util.Optional;

public interface ModifierGroupRepositoryPort {
    Optional<ModifierGroup> findById(ModifierGroupId id);
    List<ModifierGroup> findAll();
    ModifierGroup save(ModifierGroup modifierGroup);
    void deleteById(ModifierGroupId id);
}
