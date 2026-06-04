package com.restaurant.menu.domain.port.inbound;

import com.restaurant.menu.domain.model.*;
import com.restaurant.menu.domain.model.dto.*;
import java.util.List;

public interface ModifierGroupUseCases {
    ModifierGroupResult create(CreateModifierGroupCommand command);
    ModifierGroupResult getById(ModifierGroupId id);
    List<ModifierGroupResult> getAll();
    ModifierGroupResult update(ModifierGroupId id, UpdateModifierGroupCommand command);
    void delete(ModifierGroupId id);

    ModifierOptionResult addOption(ModifierGroupId groupId, CreateModifierOptionCommand command);
    ModifierOptionResult updateOption(ModifierGroupId groupId, ModifierOptionId optionId, UpdateModifierOptionCommand command);
    void removeOption(ModifierGroupId groupId, ModifierOptionId optionId);
}
