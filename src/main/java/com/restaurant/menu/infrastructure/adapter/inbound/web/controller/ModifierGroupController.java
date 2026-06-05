package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.restaurant.menu.domain.model.ModifierGroupId;
import com.restaurant.menu.domain.model.ModifierOptionId;
import com.restaurant.menu.domain.model.dto.ModifierGroupResult;
import com.restaurant.menu.domain.model.dto.ModifierOptionResult;
import com.restaurant.menu.domain.port.inbound.ModifierGroupUseCases;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateModifierGroupRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateModifierOptionRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateModifierGroupRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateModifierOptionRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.ModifierGroupResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.ModifierOptionResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.mapper.ModifierGroupApiMapper;
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
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/modifier-groups")
@SecurityRequirement(name = "bearer-jwt")
public class ModifierGroupController {

    private final ModifierGroupUseCases useCases;
    private final ModifierGroupApiMapper mapper;

    public ModifierGroupController(ModifierGroupUseCases useCases, ModifierGroupApiMapper mapper) {
        this.useCases = useCases;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ModifierGroupResponse>> create(@Valid @RequestBody CreateModifierGroupRequest request) {
        ModifierGroupResult result = useCases.create(mapper.toCommand(request));
        return ResponseEntity.created(URI.create("/api/v1/modifier-groups/" + result.id().value()))
            .body(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModifierGroupResponse>> getById(@Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        ModifierGroupResult result = useCases.getById(new ModifierGroupId(id));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModifierGroupResponse>>> getAll() {
        List<ModifierGroupResult> results = useCases.getAll();
        return ResponseEntity.ok(ApiResponse.of(results.stream().map(mapper::toResponse).toList()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ModifierGroupResponse>> update(
            @Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id, @Valid @RequestBody UpdateModifierGroupRequest request) {
        ModifierGroupResult result = useCases.update(new ModifierGroupId(id), mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        useCases.delete(new ModifierGroupId(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ModifierOptionResponse>> addOption(
            @Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id, @Valid @RequestBody CreateModifierOptionRequest request) {
        ModifierOptionResult result = useCases.addOption(new ModifierGroupId(id),
            new com.restaurant.menu.domain.model.dto.CreateModifierOptionCommand(
                request.name(), new com.restaurant.menu.domain.model.Price(request.priceAdjustment())));
        return ResponseEntity.created(URI.create("/api/v1/modifier-groups/" + id + "/options/" + result.id().value()))
            .body(ApiResponse.of(mapper.toOptionResponse(result)));
    }

    @PutMapping("/{id}/options/{optionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ModifierOptionResponse>> updateOption(
            @Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id, @Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID optionId,
            @Valid @RequestBody UpdateModifierOptionRequest request) {
        ModifierOptionResult result = useCases.updateOption(new ModifierGroupId(id), new ModifierOptionId(optionId),
            new com.restaurant.menu.domain.model.dto.UpdateModifierOptionCommand(
                request.name(), new com.restaurant.menu.domain.model.Price(request.priceAdjustment())));
        return ResponseEntity.ok(ApiResponse.of(mapper.toOptionResponse(result)));
    }

    @DeleteMapping("/{id}/options/{optionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeOption(@Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id, @Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID optionId) {
        useCases.removeOption(new ModifierGroupId(id), new ModifierOptionId(optionId));
        return ResponseEntity.noContent().build();
    }
}
