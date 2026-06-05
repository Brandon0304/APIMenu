package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.model.Category;
import com.restaurant.menu.domain.model.dto.CategoryResult;
import com.restaurant.menu.domain.model.dto.CreateCategoryCommand;
import com.restaurant.menu.domain.model.dto.UpdateCategoryCommand;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateCategoryRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateCategoryRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WebUuidMapper.class, WebValueMapper.class})
public interface CategoryApiMapper {

    @Mapping(target = "active", ignore = true)
    CreateCategoryCommand toCommand(CreateCategoryRequest request);

    UpdateCategoryCommand toCommand(UpdateCategoryRequest request);

    CategoryResponse toResponse(CategoryResult result);
}
