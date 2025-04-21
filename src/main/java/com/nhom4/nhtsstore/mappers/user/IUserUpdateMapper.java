package com.nhom4.nhtsstore.mappers.user;

import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.entities.rbac.UserHasRole;
import com.nhom4.nhtsstore.mappers.EntityCreateUpdateMapper;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Set;

@Mapper(componentModel = "spring")
@Component
public interface IUserUpdateMapper extends EntityCreateUpdateMapper<User, UserUpdateVm, UserRecordVm> {
    @Override
    User toModel(UserUpdateVm userUpdateVm);

    @Override
    @Mapping(target = "roles", source = "roles", qualifiedByName = "userHasRoleSetToRoleVmSet")
    UserUpdateVm toVm(User user);

    @Override
    UserRecordVm toVmResponse(User user);

    @Named("userHasRoleSetToRoleVmSet")
    default Set<RoleVm> userHasRoleSetToRoleVmSet(Set<UserHasRole> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> RoleVm.builder()
                        .roleId(role.getRole().getRoleId())
                        .roleName(role.getRole().getRoleName())
                        .build())
                .collect(java.util.stream.Collectors.toSet());
    }

}
