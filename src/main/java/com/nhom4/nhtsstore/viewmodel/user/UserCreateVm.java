package com.nhom4.nhtsstore.viewmodel.user;

import com.nhom4.nhtsstore.common.UserStatus;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateVm {
    String username;
    String password;
    String email;
    String fullName;
    String avatar;
    UserStatus status;
    Set<RoleVm> roles;
}
