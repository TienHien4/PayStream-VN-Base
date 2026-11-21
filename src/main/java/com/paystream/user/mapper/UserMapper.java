package com.paystream.user.mapper;

import com.paystream.user.dto.RoleDto;
import com.paystream.user.dto.UserProfileResponse;
import com.paystream.user.entity.Permission;
import com.paystream.user.entity.Role;
import com.paystream.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "permissions", source = "roles", qualifiedByName = "rolesToPermissionCodes")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToRoleDtos")
    @Mapping(target = "roleCodes", source = "roles", qualifiedByName = "rolesToRoleCodes")
    UserProfileResponse toProfileResponse(User user);

    @Named("rolesToPermissionCodes")
    default List<String> rolesToPermissionCodes(Set<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .filter(role -> role.getPermissions() != null)
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .distinct()
                .collect(Collectors.toList());
    }

    @Named("rolesToRoleDtos")
    default List<RoleDto> rolesToRoleDtos(Set<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(r -> new RoleDto(r.getId(), r.getCode(), r.getName()))
                .collect(Collectors.toList());
    }

    @Named("rolesToRoleCodes")
    default List<String> rolesToRoleCodes(Set<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
    }
}
