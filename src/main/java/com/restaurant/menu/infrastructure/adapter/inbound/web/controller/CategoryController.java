package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.model.dto.CategoryResult;
import com.restaurant.menu.domain.port.inbound.CategoryUseCases;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateCategoryRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateCategoryRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.CategoryResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.mapper.CategoryApiMapper;
import com.restaurant.menu.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/categories")
@SecurityRequirement(name = "bearer-jwt")
public class CategoryController {

    private final CategoryUseCases useCases;
    private final CategoryApiMapper mapper;

    public CategoryController(CategoryUseCases useCases, CategoryApiMapper mapper) {
        this.useCases = useCases;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResult result = useCases.create(mapper.toCommand(request));
        CategoryResponse response = mapper.toResponse(result);
        return ResponseEntity.created(URI.create("/api/v1/categories/" + result.id().value()))
            .body(ApiResponse.of(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        CategoryResult result = useCases.getById(new CategoryId(id));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        List<CategoryResult> results = useCases.getAll();
        List<CategoryResponse> responses = results.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.of(responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResult result = useCases.update(new CategoryId(id), mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateStatus(
            @Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id, @RequestBody boolean active) {
        CategoryResult result = useCases.updateStatus(new CategoryId(id), active);
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        useCases.delete(new CategoryId(id));
        return ResponseEntity.noContent().build();
    }
}
