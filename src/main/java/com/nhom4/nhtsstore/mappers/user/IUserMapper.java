package com.nhom4.nhtsstore.mappers.user;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
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

    @Mapping(source = "role", target = "roles", qualifiedByName = "mapUserRole")
    @Mapping(source = "role", target = "permissions", qualifiedByName = "mapUserPermissions")
    UserSessionVm toUserSessionVm(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "mapUserRoleWithPermission")
    UserDetailVm toUserDetailVm(User user);

    @Named("mapUserRole")
    default Set<String> mapUserRole(Role role) {
        if (role == null) {
            return Collections.emptySet();
        }
        return Collections.singleton(role.getRoleName());
    }

    @Named("mapUserRoleWithPermission")
    default RoleWithPermissionVm mapUserRoleWithPermission(Role role) {
        return RoleWithPermissionVm.builder()
                .id(role.getRoleId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissions(role.getRolePermissions().stream()
                        .map(roleHasPermission -> PermissionVm.builder()
                                .id(roleHasPermission.getPermission().getPermissionId())
                                .permissionName(roleHasPermission.getPermission().getPermissionName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Named("mapUserPermissions")
    default Set<String> mapUserPermissions(Role role) {
        if (role == null) {
            return Collections.emptySet();
        }

        return role.getRolePermissions().stream()
                .map(roleHasPermission -> roleHasPermission.getPermission().getPermissionName())
                .collect(Collectors.toSet());
    }
}