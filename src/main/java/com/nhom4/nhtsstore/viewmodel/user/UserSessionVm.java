package com.nhom4.nhtsstore.viewmodel.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSessionVm {
    String username;
    String fullName;
    String email;
    Set<String> roles;
    Set<String> permissions;
}
