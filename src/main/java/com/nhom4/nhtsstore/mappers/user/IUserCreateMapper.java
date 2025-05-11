package com.nhom4.nhtsstore.mappers.user;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.mappers.EntityCreateUpdateMapper;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.user.UserCreateVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface IUserCreateMapper extends EntityCreateUpdateMapper<User, UserCreateVm, UserRecordVm> {

    @Override
    @Mapping(target = "userId", ignore = true)
    User toModel(UserCreateVm userCreateVm);

    @AfterMapping
    default void mapRole(UserCreateVm source, @MappingTarget User target) {
        if (source.getRole() != null) {
            Role role = new Role();
            role.setRoleId(source.getRole().getRoleId());
            role.setRoleName(source.getRole().getRoleName());
            role.setDescription(source.getRole().getDescription());
            target.setRole(role);
        }
    }

    @Override
    UserCreateVm toVm(User user);

    @Override
    UserRecordVm toVmResponse(User user);

    /**
     * Updates existing User entity with values from UserCreateVm
     * Fields with null values are ignored
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void partialUpdate(@MappingTarget User user, UserCreateVm userCreateVm);

    /**
     * After partial update, update the role if provided
     */
    @AfterMapping
    default void afterPartialUpdate(UserCreateVm source, @MappingTarget User target) {
        if (source.getRole() != null) {
            Role role = new Role();
            role.setRoleId(source.getRole().getRoleId());
            role.setRoleName(source.getRole().getRoleName());
            role.setDescription(source.getRole().getDescription());
            target.setRole(role);
        }
    }
}