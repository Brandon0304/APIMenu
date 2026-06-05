package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.restaurant.menu.domain.model.IngredientId;
import com.restaurant.menu.domain.model.dto.IngredientResult;
import com.restaurant.menu.domain.port.inbound.IngredientUseCases;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateIngredientRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateIngredientRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.IngredientResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.mapper.IngredientApiMapper;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/ingredients")
@SecurityRequirement(name = "bearer-jwt")
public class IngredientController {

    private final IngredientUseCases useCases;
    private final IngredientApiMapper mapper;

    public IngredientController(IngredientUseCases useCases, IngredientApiMapper mapper) {
        this.useCases = useCases;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<IngredientResponse>> create(@Valid @RequestBody CreateIngredientRequest request) {
        IngredientResult result = useCases.create(mapper.toCommand(request));
        return ResponseEntity.created(URI.create("/api/v1/ingredients/" + result.id().value()))
            .body(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponse>> getById(@PathVariable UUID id) {
        IngredientResult result = useCases.getById(new IngredientId(id));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getAll() {
        List<IngredientResult> results = useCases.getAll();
        return ResponseEntity.ok(ApiResponse.of(results.stream().map(mapper::toResponse).toList()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<IngredientResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateIngredientRequest request) {
        IngredientResult result = useCases.update(new IngredientId(id), mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        useCases.delete(new IngredientId(id));
        return ResponseEntity.noContent().build();
    }
}
