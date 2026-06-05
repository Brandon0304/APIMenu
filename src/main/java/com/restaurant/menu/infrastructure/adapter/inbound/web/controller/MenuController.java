package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.restaurant.menu.domain.model.MenuId;
import com.restaurant.menu.domain.model.MenuSectionId;
import com.restaurant.menu.domain.model.dto.MenuResult;
import com.restaurant.menu.domain.model.dto.MenuSectionResult;
import com.restaurant.menu.domain.port.inbound.MenuUseCases;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.AddMenuSectionRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateMenuRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateMenuRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.MenuResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.mapper.MenuApiMapper;
import com.restaurant.menu.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/menus")
@SecurityRequirement(name = "bearer-jwt")
public class MenuController {

    private final MenuUseCases useCases;
    private final MenuApiMapper mapper;

    public MenuController(MenuUseCases useCases, MenuApiMapper mapper) {
        this.useCases = useCases;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuResponse>> create(@Valid @RequestBody CreateMenuRequest request) {
        MenuResult result = useCases.create(mapper.toCommand(request));
        return ResponseEntity.created(URI.create("/api/v1/menus/" + result.id().value()))
            .body(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> getById(@PathVariable UUID id) {
        MenuResult result = useCases.getById(new MenuId(id));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAll(@RequestParam(defaultValue = "false") boolean active) {
        List<MenuResult> results = useCases.getAll(active);
        return ResponseEntity.ok(ApiResponse.of(results.stream().map(mapper::toResponse).toList()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateMenuRequest request) {
        MenuResult result = useCases.update(new MenuId(id), mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        useCases.delete(new MenuId(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.MenuSectionResponse>> addSection(
            @PathVariable UUID id, @Valid @RequestBody AddMenuSectionRequest request) {
        MenuSectionResult result = useCases.addSection(new MenuId(id),
            new com.restaurant.menu.domain.model.CategoryId(request.categoryId()), request.displayOrder());
        return ResponseEntity.created(URI.create("/api/v1/menus/" + id + "/sections/" + result.id().value()))
            .body(ApiResponse.of(mapper.toSectionResponse(result)));
    }

    @DeleteMapping("/{id}/sections/{sectionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeSection(@PathVariable UUID id, @PathVariable UUID sectionId) {
        useCases.removeSection(new MenuId(id), new MenuSectionId(sectionId));
        return ResponseEntity.noContent().build();
    }
}
