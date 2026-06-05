package com.restaurant.menu.domain.port.outbound;

import com.restaurant.menu.domain.model.User;
import com.restaurant.menu.domain.model.UserId;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(String email);
    User save(User user);
    boolean existsByEmail(String email);
}
