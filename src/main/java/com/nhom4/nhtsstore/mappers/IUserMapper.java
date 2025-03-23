package com.nhom4.nhtsstore.mappers;


import com.nhom4.nhtsstore.entities.Permission;
import com.nhom4.nhtsstore.entities.Role;
import com.nhom4.nhtsstore.entities.User;
import com.nhom4.nhtsstore.viewmodel.user.UserCreateVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", implementationName = "UserMapperImpl")
public interface IUserMapper {

    // Mapping User → UserSessionVm
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleNames")
    @Mapping(source = "roles", target = "permissions", qualifiedByName = "rolesToPermissions")
    UserSessionVm toUserSessionVm(User user);

    // Mapping UserCreateVm → User (for user creation)
    @Mapping(source = "roles", target = "roles", qualifiedByName = "roleNamesToRoles")
    User toUser(UserCreateVm userCreateVm);

    // Mapping UserRecordVm → User (for user updates)
    void updateUser(@MappingTarget User user, UserRecordVm userRecordVm);

    // Mapping User → UserRecordVm (returning a simplified DTO)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "fullName")
    UserRecordVm toUserRecord(User user);

    // Converts Set<Role> → Set<String> (role names)
    @Named("rolesToRoleNames")
    static Set<String> rolesToRoleNames(Set<Role> roles) {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    }

    // Converts Set<String> → Set<Role> (role entities)
    @Named("roleNamesToRoles")
    static Set<Role> roleNamesToRoles(Set<String> roleNames) {
        if (roleNames == null) return Collections.emptySet();
        return roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
    }

    // Converts Set<Role> → Set<String> (permissions)
    @Named("rolesToPermissions")
    static Set<String> rolesToPermissions(Set<Role> roles) {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet());
    }
}
