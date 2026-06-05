package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.model.dto.AllergenResult;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.AllergenResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {WebUuidMapper.class, WebValueMapper.class})
public interface AllergenApiMapper {

    AllergenResponse toResponse(AllergenResult result);
}
