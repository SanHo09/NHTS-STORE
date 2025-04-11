package com.nhom4.nhtsstore.viewmodel.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateVm {
    String username;
    String password;
    String email;
    String fullName;
    Set<String> roles;
    Set<String> permissions;

}
