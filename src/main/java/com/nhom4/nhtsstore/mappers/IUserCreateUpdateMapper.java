package com.nhom4.nhtsstore.mappers;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.entities.rbac.UserHasRole;
import com.nhom4.nhtsstore.viewmodel.permission.PermissionVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import com.nhom4.nhtsstore.viewmodel.user.UserCreateVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@Component
public interface IUserCreateUpdateMapper extends EntityCreateUpdateMapper<User, UserCreateVm, UserRecordVm> {

    /**
     * Converts UserCreateVm to User entity
     * Note: Roles relationship needs special handling
     */
    @Override
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toModel(UserCreateVm userCreateVm);

    /**
     * After mapping, handle the roles relationship
     */
    @AfterMapping
    default void mapRoles(UserCreateVm source, @MappingTarget User target) {
        if (source.getRoles() != null) {
            Set<UserHasRole> userHasRoles = new HashSet<>();

            source.getRoles().forEach(roleVm -> {
                Role role = new Role();
                role.setRoleId(roleVm.getId());
                role.setRoleName(roleVm.getRoleName());
                role.setDescription(roleVm.getDescription());

                UserHasRole userHasRole = new UserHasRole();
                userHasRole.setRole(role);
                userHasRole.setUser(target);

                userHasRoles.add(userHasRole);
            });

            target.setRoles(userHasRoles);
        }
    }

    /**
     * Converts User entity to UserCreateVm
     */
    @Override
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapUserRolesToVm")
    UserCreateVm toVm(User user);

    /**
     * Maps User entity to UserResponseVm for API responses
     */
    @Override
    @Mapping(source = "userId", target = "id")
    @Mapping(source = "status", target = "status")
    UserRecordVm toVmResponse(User user);

    /**
     * Helper method to map UserHasRole to RoleWithPermissionVm
     */
    @Named("mapUserRolesToVm")
    default Set<RoleWithPermissionVm> mapUserRolesToVm(Set<UserHasRole> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }

        return roles.stream()
                .map(userHasRole -> {
                    Role role = userHasRole.getRole();
                    RoleWithPermissionVm roleVm = new RoleWithPermissionVm();
                    roleVm.setId(role.getRoleId());
                    roleVm.setRoleName(role.getRoleName());
                    roleVm.setDescription(role.getDescription());

                    // Map permissions
                    Set<PermissionVm> permissionVms = role.getRoles().stream()
                            .map(roleHasPermission -> {
                                Permission permission = roleHasPermission.getPermission();
                                return new PermissionVm(
                                        permission.getPermissionId(),
                                        permission.getPermissionName(),
                                        permission.getDescription()
                                );
                            })
                            .collect(Collectors.toSet());

                    roleVm.setPermissions(permissionVms);
                    return roleVm;
                })
                .collect(Collectors.toSet());
    }

    /**
     * Helper method to map UserHasRole to RoleResponseVm for API responses
     */
    @Named("mapUserRolesToResponse")
    default Set<RoleWithPermissionVm> mapUserRolesToResponse(Set<UserHasRole> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }

        return roles.stream()
                .map(userHasRole -> {
                    Role role = userHasRole.getRole();
                    RoleWithPermissionVm roleResponseVm = new RoleWithPermissionVm();
                    roleResponseVm.setId(role.getRoleId());
                    roleResponseVm.setRoleName(role.getRoleName());
                    roleResponseVm.setDescription(role.getDescription());

                    // Map permissions to permission response objects
                    Set<PermissionVm> permissionResponseVms = role.getRoles().stream()
                            .map(roleHasPermission -> {
                                Permission permission = roleHasPermission.getPermission();
                                PermissionVm permissionResponseVm = new PermissionVm();
                                permissionResponseVm.setId(permission.getPermissionId());
                                permissionResponseVm.setPermissionName(permission.getPermissionName());
                                permissionResponseVm.setDescription(permission.getDescription());
                                return permissionResponseVm;
                            })
                            .collect(Collectors.toSet());

                    roleResponseVm.setPermissions(permissionResponseVms);
                    return roleResponseVm;
                })
                .collect(Collectors.toSet());
    }

    /**
     * Updates existing User entity with values from UserCreateVm
     * Fields with null values are ignored
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void partialUpdate(@MappingTarget User user, UserCreateVm userCreateVm);

    /**
     * After partial update, update the roles if provided
     */
    @AfterMapping
    default void afterPartialUpdate(UserCreateVm source, @MappingTarget User target) {
        if (source.getRoles() != null && !source.getRoles().isEmpty()) {
            // Clear existing roles and add new ones
            target.getRoles().clear();

            Set<UserHasRole> userHasRoles = new HashSet<>();
            source.getRoles().forEach(roleVm -> {
                Role role = new Role();
                role.setRoleId(roleVm.getId());
                role.setRoleName(roleVm.getRoleName());
                role.setDescription(roleVm.getDescription());

                UserHasRole userHasRole = new UserHasRole();
                userHasRole.setRole(role);
                userHasRole.setUser(target);

                userHasRoles.add(userHasRole);
            });

            target.getRoles().addAll(userHasRoles);
        }
    }
}
