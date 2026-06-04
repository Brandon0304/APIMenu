package com.restaurant.menu.domain.port.inbound;

import com.restaurant.menu.domain.model.*;
import com.restaurant.menu.domain.model.dto.AllergenResult;
import java.util.List;

public interface AllergenUseCases {
    AllergenResult getById(AllergenId id);
    List<AllergenResult> getAll();
}
