package com.nhom4.nhtsstore.mappers.user;


import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.entities.rbac.UserHasRole;
import com.nhom4.nhtsstore.mappers.BaseMapper;
import com.nhom4.nhtsstore.viewmodel.permission.PermissionVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@Component
public interface IUserMapper extends BaseMapper<User, UserRecordVm> {

    /**
     * Maps User entity to UserSessionVm for storing user session information
     * @param user The user entity
     * @return UserSessionVm with session information
     */
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapUserRoles")
    @Mapping(source = "roles", target = "permissions", qualifiedByName = "mapUserPermissions")
    UserSessionVm toUserSessionVm(User user);

    @Mapping( source = "userId", target = "userId")
    @Mapping( source = "username", target = "username")
    @Mapping( source = "fullName", target = "fullName")
    @Mapping( source = "email", target = "email")
    @Mapping( source = "avatar", target = "avatar")
    @Mapping( source = "status", target = "status")
    @Mapping( source = "roles", target = "roles", qualifiedByName = "mapUserRolesWithPermission")
    UserDetailVm toUserDetailVm(User user);
    /**
     * Extract role names from UserHasRole entities
     */
    @Named("mapUserRoles")
    default Set<String> mapUserRoles(Set<UserHasRole> userHasRoles) {
        if (userHasRoles == null) {
            return Collections.emptySet();
        }

        return userHasRoles.stream()
                .map(userHasRole -> userHasRole.getRole().getRoleName())
                .collect(Collectors.toSet());
    }
    @Named("mapUserRolesWithPermission")
    default Set<RoleWithPermissionVm> mapUserRolesWithPermission(Set<UserHasRole> userHasRoles) {
        if (userHasRoles == null) {
            return Collections.emptySet();
        }

        return userHasRoles.stream()
                .map(userHasRole -> RoleWithPermissionVm.builder()
                        .id(userHasRole.getRole().getRoleId())
                        .roleName(userHasRole.getRole().getRoleName())
                        .permissions(userHasRole.getRole().getRolePermissions().stream()
                                .map(roleHasPermission ->
                                                PermissionVm.builder()
                                                        .id(roleHasPermission.getPermission().getPermissionId())
                                                        .permissionName(roleHasPermission.getPermission().getPermissionName())
                                                        .description(roleHasPermission.getPermission().getDescription())
                                                        .build()
                                        )
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet());
    }

    /**
     * Extract permissions from user roles
     */
    @Named("mapUserPermissions")
    default Set<String> mapUserPermissions(Set<UserHasRole> userHasRoles) {
        if (userHasRoles == null) {
            return Collections.emptySet();
        }

        return userHasRoles.stream()
                .map(UserHasRole::getRole)
                .flatMap(role -> role.getRolePermissions().stream()) // Get RoleHasPermission objects
                .map(roleHasPermission -> roleHasPermission.getPermission().getPermissionName())
                .collect(Collectors.toSet());
    }

}