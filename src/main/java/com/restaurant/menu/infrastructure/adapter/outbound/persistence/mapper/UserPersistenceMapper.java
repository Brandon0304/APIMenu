package com.restaurant.menu.infrastructure.adapter.outbound.persistence.mapper;

import com.restaurant.menu.domain.model.User;
import com.restaurant.menu.domain.model.UserId;
import com.restaurant.menu.domain.model.UserRole;
import com.restaurant.menu.infrastructure.adapter.outbound.persistence.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserJpaEntity toJpaEntity(User domain);

    @Mapping(target = "password", source = "password")
    User toDomain(UserJpaEntity jpaEntity);

    default String roleToString(UserRole role) {
        return role != null ? role.name() : null;
    }

    default UserRole stringToRole(String role) {
        return role != null ? UserRole.valueOf(role) : null;
    }

    default UserId uuidToUserId(java.util.UUID uuid) {
        return uuid != null ? new UserId(uuid) : null;
    }

    default java.util.UUID userIdToUuid(UserId id) {
        return id != null ? id.value() : null;
    }
}
