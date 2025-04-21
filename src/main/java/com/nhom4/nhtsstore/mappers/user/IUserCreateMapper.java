package com.nhom4.nhtsstore.mappers.user;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.entities.rbac.UserHasRole;
import com.nhom4.nhtsstore.mappers.EntityCreateUpdateMapper;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
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
public interface IUserCreateMapper extends EntityCreateUpdateMapper<User, UserCreateVm, UserRecordVm> {

    @Override
    @Mapping(target = "userId", ignore = true)

    User toModel(UserCreateVm userCreateVm);


    @AfterMapping
    default void mapRoles(UserCreateVm source, @MappingTarget User target) {
        if (source.getRoles() != null) {
            Set<UserHasRole> userHasRoles = new HashSet<>();

            source.getRoles().forEach(roleVm -> {
                Role role = new Role();
                role.setRoleId(roleVm.getRoleId());
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

    @Override
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapUserRolesToVm")
    UserCreateVm toVm(User user);


    @Override
    UserRecordVm toVmResponse(User user);


    @Named("mapUserRolesToVm")
    default Set<RoleVm> mapUserRolesToVm(Set<UserHasRole> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }

        return roles.stream()
                .map(userHasRole -> {
                    RoleVm roleVm = new RoleVm();
                    Role role = userHasRole.getRole();
                    roleVm.setRoleId(role.getRoleId());
                    roleVm.setRoleName(role.getRoleName());
                    roleVm.setDescription(role.getDescription());

                    return roleVm;
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
                role.setRoleId(roleVm.getRoleId());
                role.setRoleName(roleVm.getRoleName());
                role.setDescription(roleVm.getDescription());

                UserHasRole userHasRole = new UserHasRole();
                userHasRole.setRole(role);
//                userHasRole.setUser(target);

                userHasRoles.add(userHasRole);
            });

            target.getRoles().addAll(userHasRoles);
        }
    }
}
