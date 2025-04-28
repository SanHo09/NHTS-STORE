package com.nhom4.nhtsstore.mappers.user;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.mappers.EntityCreateUpdateMapper;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface IUserUpdateMapper extends EntityCreateUpdateMapper<User, UserUpdateVm, UserRecordVm> {
    @Override
    @Mapping(target = "role", source = "role")
    User toModel(UserUpdateVm userUpdateVm);

    @Override
    UserUpdateVm toVm(User user);

    @Override
    UserRecordVm toVmResponse(User user);

    @AfterMapping
    default void mapRole(UserUpdateVm source, @MappingTarget User target) {
        if (source.getRole() != null) {
            Role role = new Role();
            role.setRoleId(source.getRole().getRoleId());
            role.setRoleName(source.getRole().getRoleName());
            role.setDescription(source.getRole().getDescription());
            target.setRole(role);
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void partialUpdate(@MappingTarget User user, UserUpdateVm userUpdateVm);

    @AfterMapping
    default void afterPartialUpdate(UserUpdateVm source, @MappingTarget User target) {
        if (source.getRole() != null) {
            Role role = new Role();
            role.setRoleId(source.getRole().getRoleId());
            role.setRoleName(source.getRole().getRoleName());
            role.setDescription(source.getRole().getDescription());
            target.setRole(role);
        }
    }
}