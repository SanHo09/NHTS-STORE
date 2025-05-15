package com.nhom4.nhtsstore.viewmodel.user;


import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailVm {
    Long userId;
    String username;
    String password;
    String email;
    String fullName;
    byte[] avatar;
    boolean active;
    RoleWithPermissionVm role;
}
