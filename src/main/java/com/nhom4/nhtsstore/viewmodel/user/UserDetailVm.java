package com.nhom4.nhtsstore.viewmodel.user;


import com.nhom4.nhtsstore.common.UserStatus;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailVm {
    Integer userId;
    String username;
    String password;
    String email;
    String fullName;
    String avatar;
    UserStatus status;
    Set<RoleWithPermissionVm> roles;
}
