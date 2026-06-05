package com.restaurant.menu.application.service;

import com.restaurant.menu.domain.exception.AllergenNotFoundException;
import com.restaurant.menu.domain.model.Allergen;
import com.restaurant.menu.domain.model.AllergenId;
import com.restaurant.menu.domain.model.dto.AllergenResult;
import com.restaurant.menu.domain.port.inbound.AllergenUseCases;
import com.restaurant.menu.domain.port.outbound.AllergenRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AllergenService implements AllergenUseCases {

    private final AllergenRepositoryPort repository;

    public AllergenService(AllergenRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public AllergenResult getById(AllergenId id) {
        return repository.findById(id)
            .map(AllergenService::toResult)
            .orElseThrow(() -> new AllergenNotFoundException(id));
    }

    @Override
    public List<AllergenResult> getAll() {
        return repository.findAll().stream()
            .map(AllergenService::toResult)
            .toList();
    }

    static AllergenResult toResult(Allergen allergen) {
        return new AllergenResult(
            allergen.getId(),
            allergen.getName(),
            allergen.getDescription(),
            allergen.getIcon()
        );
    }
}
