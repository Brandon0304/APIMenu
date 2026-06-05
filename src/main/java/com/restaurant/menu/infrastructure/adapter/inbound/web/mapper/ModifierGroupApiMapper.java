package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.model.dto.CreateModifierGroupCommand;
import com.restaurant.menu.domain.model.dto.ModifierGroupResult;
import com.restaurant.menu.domain.model.dto.ModifierOptionResult;
import com.restaurant.menu.domain.model.dto.UpdateModifierGroupCommand;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateModifierGroupRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateModifierGroupRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.ModifierGroupResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.ModifierOptionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {WebUuidMapper.class, WebValueMapper.class})
public interface ModifierGroupApiMapper {

    CreateModifierGroupCommand toCommand(CreateModifierGroupRequest request);

    UpdateModifierGroupCommand toCommand(UpdateModifierGroupRequest request);

    ModifierGroupResponse toResponse(ModifierGroupResult result);

    ModifierOptionResponse toOptionResponse(ModifierOptionResult result);
}
