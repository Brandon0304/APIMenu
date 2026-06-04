package com.restaurant.menu.domain.port.outbound;

import com.restaurant.menu.domain.model.*;
import java.util.List;
import java.util.Optional;

public interface MenuRepositoryPort {
    Optional<Menu> findById(MenuId id);
    List<Menu> findAll(boolean activeOnly);
    Menu save(Menu menu);
    void deleteById(MenuId id);
}
