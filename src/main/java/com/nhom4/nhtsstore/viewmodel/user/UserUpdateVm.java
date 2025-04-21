package com.nhom4.nhtsstore.viewmodel.user;

import com.nhom4.nhtsstore.common.UserStatus;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateVm {
    Integer userId;
    String username;
    String email;
    String fullName;
    String password;
    String newPassword;
    String confirmPassword;
    String avatar;
    UserStatus status;
    Set<RoleVm> roles;
}
