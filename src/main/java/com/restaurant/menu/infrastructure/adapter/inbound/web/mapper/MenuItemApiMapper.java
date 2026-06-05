package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.model.dto.CreateMenuItemCommand;
import com.restaurant.menu.domain.model.dto.MenuItemResult;
import com.restaurant.menu.domain.model.dto.UpdateMenuItemCommand;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateMenuItemRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateMenuItemRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.MenuItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WebUuidMapper.class, WebValueMapper.class})
public interface MenuItemApiMapper {

    @Mapping(target = "price", expression = "java(webValueMapper.toPrice(request.price()))")
    @Mapping(target = "categoryId", expression = "java(webUuidMapper.toCategoryId(request.categoryId()))")
    @Mapping(target = "preparationTime", expression = "java(webValueMapper.toPreparationTime(request.preparationTimeMinutes()))")
    @Mapping(target = "imageUrl", expression = "java(webValueMapper.toImageUrl(request.imageUrl()))")
    CreateMenuItemCommand toCommand(CreateMenuItemRequest request);

    @Mapping(target = "price", expression = "java(webValueMapper.toPrice(request.price()))")
    @Mapping(target = "categoryId", expression = "java(webUuidMapper.toCategoryId(request.categoryId()))")
    @Mapping(target = "preparationTime", expression = "java(webValueMapper.toPreparationTime(request.preparationTimeMinutes()))")
    @Mapping(target = "imageUrl", expression = "java(webValueMapper.toImageUrl(request.imageUrl()))")
    UpdateMenuItemCommand toCommand(UpdateMenuItemRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "categoryId", target = "categoryId")
    @Mapping(source = "preparationTime", target = "preparationTimeMinutes")
    @Mapping(source = "imageUrl", target = "imageUrl")
    MenuItemResponse toResponse(MenuItemResult result);
}
