package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.model.dto.CreateIngredientCommand;
import com.restaurant.menu.domain.model.dto.IngredientResult;
import com.restaurant.menu.domain.model.dto.UpdateIngredientCommand;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateIngredientRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateIngredientRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.IngredientResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {WebUuidMapper.class, WebValueMapper.class})
public interface IngredientApiMapper {

    CreateIngredientCommand toCommand(CreateIngredientRequest request);

    UpdateIngredientCommand toCommand(UpdateIngredientRequest request);

    IngredientResponse toResponse(IngredientResult result);
}
