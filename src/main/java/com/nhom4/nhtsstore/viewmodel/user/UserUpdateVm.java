package com.nhom4.nhtsstore.viewmodel.user;

import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateVm {
    Long userId;
    String username;
    String email;
    String fullName;
    byte[] avatar;
    boolean active;
    String password;
    RoleVm role;
}
