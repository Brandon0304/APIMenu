package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.restaurant.menu.domain.model.AllergenId;
import com.restaurant.menu.domain.model.dto.AllergenResult;
import com.restaurant.menu.domain.port.inbound.AllergenUseCases;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.response.AllergenResponse;
import com.restaurant.menu.infrastructure.adapter.inbound.web.mapper.AllergenApiMapper;
import com.restaurant.menu.shared.dto.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/allergens")
@SecurityRequirement(name = "bearer-jwt")
public class AllergenController {

    private final AllergenUseCases useCases;
    private final AllergenApiMapper mapper;

    public AllergenController(AllergenUseCases useCases, AllergenApiMapper mapper) {
        this.useCases = useCases;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AllergenResponse>> getById(@Parameter(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        AllergenResult result = useCases.getById(new AllergenId(id));
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AllergenResponse>>> getAll() {
        List<AllergenResult> results = useCases.getAll();
        return ResponseEntity.ok(ApiResponse.of(results.stream().map(mapper::toResponse).toList()));
    }
}
