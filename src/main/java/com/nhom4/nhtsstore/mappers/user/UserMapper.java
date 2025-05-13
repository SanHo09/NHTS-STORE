package com.nhom4.nhtsstore.mappers.user;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.viewmodel.permission.PermissionVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import com.nhom4.nhtsstore.viewmodel.user.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manual static mapper methods for User-related conversions
 */
public class UserMapper {

    /**
     * Convert User entity to UserSessionVm for authentication
     */
    public static UserSessionVm toUserSessionVm(User user) {
        if (user == null) return null;

        UserSessionVm sessionVm = new UserSessionVm();
        sessionVm.setUserId(user.getUserId());
        sessionVm.setUsername(user.getUsername());
        sessionVm.setFullName(user.getFullName());
        sessionVm.setEmail(user.getEmail());
        sessionVm.setAvatar(user.getAvatar());
        sessionVm.setActive(user.isActive());

        if (user.getRole() != null) {
            sessionVm.setRole(user.getRole().getRoleName());
            Set<String> permissions = user.getRole().getRolePermissions().stream()
                    .map(roleHasPermission -> roleHasPermission.getPermission().getPermissionName())
                    .collect(Collectors.toSet());
            sessionVm.setPermissions(permissions);
        }

        return sessionVm;
    }

    /**
     * Convert User entity to UserDetailVm
     */
    public static UserDetailVm toUserDetailVm(User user) {
        if (user == null) return null;

        UserDetailVm detailVm = new UserDetailVm();
        detailVm.setUserId(user.getUserId());
        detailVm.setUsername(user.getUsername());
        detailVm.setFullName(user.getFullName());
        detailVm.setEmail(user.getEmail());
        detailVm.setAvatar(user.getAvatar());
        detailVm.setActive(user.isActive());

        // Map role if exists
        if (user.getRole() != null) {
            RoleWithPermissionVm roleVm = new RoleWithPermissionVm();
            roleVm.setRoleName(user.getRole().getRoleName());
            roleVm.setId(user.getRole().getRoleId());
            roleVm.setPermissions(user.getRole().getRolePermissions().stream().map(roleHasPermission -> {
                return PermissionVm.builder()
                        .id(roleHasPermission.getPermission().getId())
                        .permissionName(roleHasPermission.getPermission().getPermissionName())
                        .build();
            }).collect(Collectors.toSet()));
            detailVm.setRole(roleVm);
        }

        // Add audit fields if needed
//        detailVm.setCreatedBy(user.getCreatedBy());
//        detailVm.setCreatedOn(user.getCreatedOn());
//        detailVm.setLastModifiedBy(user.getLastModifiedBy());
//        detailVm.setLastModifiedOn(user.getLastModifiedOn());

        return detailVm;
    }

    /**
     * Convert User entity to UserRecordVm for listings
     */
    public static UserRecordVm toVm(User user) {
        if (user == null) return null;

        UserRecordVm recordVm = new UserRecordVm();
        recordVm.setUserId(user.getUserId());
        recordVm.setUsername(user.getUsername());
        recordVm.setFullName(user.getFullName());
        recordVm.setEmail(user.getEmail());
        recordVm.setAvatar(user.getAvatar());
        recordVm.setActive(user.isActive());

        return recordVm;
    }

    /**
     * Convert UserCreateVm to User entity
     */
    public static User toModel(UserCreateVm createVm) {
        if (createVm == null) return null;

        User user = new User();
        user.setUsername(createVm.getUsername());
        user.setPassword(createVm.getPassword()); // Note: Will be encoded by the service
        user.setFullName(createVm.getFullName());
        user.setEmail(createVm.getEmail());
        user.setAvatar(createVm.getAvatar());
        user.setActive(createVm.isActive());

        // Set role if provided
        if (createVm.getRole() != null) {
            Role role = new Role();
            role.setRoleId(createVm.getRole().getRoleId());
            user.setRole(role);
        }

        return user;
    }

    /**
     * Convert UserUpdateVm to User entity
     */
    public static User toModel(UserUpdateVm updateVm) {
        if (updateVm == null) return null;

        User user = new User();
        user.setUserId(updateVm.getUserId());
        user.setUsername(updateVm.getUsername());
        user.setFullName(updateVm.getFullName());
        user.setEmail(updateVm.getEmail());
        user.setAvatar(updateVm.getAvatar());
        user.setActive(updateVm.isActive());

        // Set role if provided
        if (updateVm.getRole() != null) {
            Role role = new Role();
            role.setRoleId(updateVm.getRole().getRoleId());
            user.setRole(role);
        }

        // Set password if provided
        if (updateVm.getPassword() != null && !updateVm.getPassword().isEmpty()) {
            user.setPassword(updateVm.getPassword());
        }

        return user;
    }
}