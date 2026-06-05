package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.restaurant.menu.domain.model.AllergenId;
import com.restaurant.menu.domain.model.CategoryId;
import com.restaurant.menu.domain.model.IngredientId;
import com.restaurant.menu.domain.model.MenuItemId;
import com.restaurant.menu.domain.model.ModifierGroupId;
import com.restaurant.menu.domain.model.dto.MenuItemResult;
import com.restaurant.menu.domain.port.inbound.MenuItemUseCases;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.AddIngredientRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateMenuItemRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateMenuItemRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.UpdateNutritionalInfoRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.MenuItemResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.NutritionalInfoResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.mapper.MenuItemApiMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menu-items")
public class MenuItemController {

    private final MenuItemUseCases useCases;
    private final MenuItemApiMapper mapper;

    public MenuItemController(MenuItemUseCases useCases, MenuItemApiMapper mapper) {
        this.useCases = useCases;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> create(@Valid @RequestBody CreateMenuItemRequest request) {
        MenuItemResult result = useCases.create(mapper.toCommand(request));
        return ResponseEntity.created(URI.create("/api/v1/menu-items/" + result.id().value()))
            .body(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getById(@PathVariable UUID id) {
        MenuItemResult result = useCases.getById(new MenuItemId(id));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID categoryId) {
        List<MenuItemResult> results;
        if (categoryId != null) {
            results = useCases.getByCategoryId(new CategoryId(categoryId));
        } else {
            results = useCases.getAll(page, size);
        }
        return ResponseEntity.ok(ApiResponse.of(results.stream().map(mapper::toResponse).toList()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateMenuItemRequest request) {
        MenuItemResult result = useCases.update(new MenuItemId(id), mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateStatus(
            @PathVariable UUID id, @RequestBody boolean active) {
        MenuItemResult result = useCases.updateStatus(new MenuItemId(id), active);
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        useCases.delete(new MenuItemId(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ingredients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addIngredient(
            @PathVariable UUID id, @Valid @RequestBody AddIngredientRequest request) {
        useCases.addIngredient(new MenuItemId(id),
            new IngredientId(request.ingredientId()), request.quantity().doubleValue());
        return ResponseEntity.created(URI.create("/api/v1/menu-items/" + id + "/ingredients")).build();
    }

    @DeleteMapping("/{id}/ingredients/{ingredientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeIngredient(@PathVariable UUID id, @PathVariable UUID ingredientId) {
        useCases.removeIngredient(new MenuItemId(id), new IngredientId(ingredientId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/modifier-groups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignModifierGroup(@PathVariable UUID id, @RequestBody UUID modifierGroupId) {
        useCases.assignModifierGroup(new MenuItemId(id), new ModifierGroupId(modifierGroupId));
        return ResponseEntity.created(URI.create("/api/v1/menu-items/" + id + "/modifier-groups")).build();
    }

    @DeleteMapping("/{id}/modifier-groups/{groupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeModifierGroup(@PathVariable UUID id, @PathVariable UUID groupId) {
        useCases.removeModifierGroup(new MenuItemId(id), new ModifierGroupId(groupId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/allergens")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addAllergen(@PathVariable UUID id, @RequestBody UUID allergenId) {
        useCases.addAllergen(new MenuItemId(id), new AllergenId(allergenId));
        return ResponseEntity.created(URI.create("/api/v1/menu-items/" + id + "/allergens")).build();
    }

    @DeleteMapping("/{id}/allergens/{allergenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeAllergen(@PathVariable UUID id, @PathVariable UUID allergenId) {
        useCases.removeAllergen(new MenuItemId(id), new AllergenId(allergenId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/nutritional-info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NutritionalInfoResponse>> updateNutritionalInfo(
            @PathVariable UUID id, @Valid @RequestBody UpdateNutritionalInfoRequest request) {
        var command = new com.restaurant.menu.domain.model.dto.UpdateNutritionalInfoCommand(
            request.calories(), request.proteinGrams(), request.carbsGrams(),
            request.fatGrams(), request.fiberGrams(), request.sodiumMg());
        var result = useCases.updateNutritionalInfo(new MenuItemId(id), command);
        var response = new NutritionalInfoResponse(
            result.calories(), result.proteinGrams(), result.carbsGrams(),
            result.fatGrams(), result.fiberGrams(), result.sodiumMg());
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
