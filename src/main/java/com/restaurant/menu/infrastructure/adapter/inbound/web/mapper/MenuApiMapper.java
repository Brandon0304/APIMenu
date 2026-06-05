package com.restaurant.menu.infrastructure.adapter.inbound.web.mapper;

import com.restaurant.menu.domain.model.ValidityPeriod;
import com.restaurant.menu.domain.model.dto.CreateMenuCommand;
import com.restaurant.menu.domain.model.dto.MenuResult;
import com.restaurant.menu.domain.model.dto.MenuSectionResult;
import com.restaurant.menu.domain.model.dto.UpdateMenuCommand;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateMenuRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateMenuRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.MenuResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.MenuSectionResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MenuApiMapper {

    private final WebUuidMapper webUuidMapper;
    private final WebValueMapper webValueMapper;

    public MenuApiMapper(WebUuidMapper webUuidMapper, WebValueMapper webValueMapper) {
        this.webUuidMapper = webUuidMapper;
        this.webValueMapper = webValueMapper;
    }

    public CreateMenuCommand toCommand(CreateMenuRequest request) {
        if (request == null) return null;
        ValidityPeriod validityPeriod = webValueMapper.toValidityPeriod(request.validFrom(), request.validTo());
        return new CreateMenuCommand(request.name(), request.description(), validityPeriod);
    }

    public UpdateMenuCommand toCommand(UpdateMenuRequest request) {
        if (request == null) return null;
        ValidityPeriod validityPeriod = webValueMapper.toValidityPeriod(request.validFrom(), request.validTo());
        return new UpdateMenuCommand(request.name(), request.description(), validityPeriod);
    }

    public MenuResponse toResponse(MenuResult result) {
        if (result == null) return null;
        List<MenuSectionResponse> sections = result.sections().stream()
            .map(this::toSectionResponse)
            .toList();
        return new MenuResponse(
            webUuidMapper.toUuid(result.id()),
            result.name(),
            result.description(),
            result.active(),
            webValueMapper.toValidFrom(result.validityPeriod()),
            webValueMapper.toValidTo(result.validityPeriod()),
            sections,
            result.createdAt(),
            result.updatedAt()
        );
    }

    public MenuSectionResponse toSectionResponse(MenuSectionResult result) {
        if (result == null) return null;
        return new MenuSectionResponse(
            webUuidMapper.toUuid(result.id()),
            webUuidMapper.toUuid(result.categoryId()),
            result.displayOrder()
        );
    }
}
