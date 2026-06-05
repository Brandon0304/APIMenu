package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.User;
import com.restaurant.menu.domain.model.UserId;
import com.restaurant.menu.domain.model.UserRole;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.UserJpaEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) return null;
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.id().value());
        entity.setEmail(domain.email());
        entity.setName(domain.name());
        entity.setRole(domain.role().name());
        entity.setActive(domain.active());
        return entity;
    }

    public User toDomain(UserJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new User(
            new UserId(jpaEntity.getId()),
            jpaEntity.getEmail(),
            jpaEntity.getPassword(),
            jpaEntity.getName(),
            UserRole.valueOf(jpaEntity.getRole()),
            jpaEntity.isActive()
        );
    }
}
