package com.nhom4.nhtsstore.viewmodel.user;

import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    byte[] avatar;
    boolean active;
    RoleVm role;
}
