package com.nhom4.nhtsstore.mappers;


import com.nhom4.nhtsstore.entities.Permission;
import com.nhom4.nhtsstore.entities.Role;
import com.nhom4.nhtsstore.entities.User;
import com.nhom4.nhtsstore.viewmodel.user.UserCreatVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", implementationName = "UserMapperImpl")
public interface IUserMapper {


    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleNames")
    @Mapping(source = "roles", target = "permissions", qualifiedByName = "rolesToPermissions")
    UserSessionVm toUserSessionVm(User user);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "roleNamesToRoles")
    void createUser(@MappingTarget User user, UserCreatVm userCreatVm);

    void updateUser(@MappingTarget User user, UserRecordVm userRecordVm);

    UserRecordVm toUserRecord(User user);

    @Named("rolesToRoleNames")
    default Set<String> rolesToRoleNames(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    }
    @Named("roleNamesToRoles")
    default Set<Role> roleNamesToRoles(Set<String> roleNames) {
        if (roleNames == null) return null;
        return roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
    }
    @Named("rolesToPermissions")
    default Set<String> rolesToPermissions(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet());
    }


}