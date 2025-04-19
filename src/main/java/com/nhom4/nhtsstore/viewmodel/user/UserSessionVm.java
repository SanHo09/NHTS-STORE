package com.nhom4.nhtsstore.viewmodel.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionVm {
    Integer userId;
    String username;
    String fullName;
    String email;
    Set<String> roles;
    Set<String> permissions;
}
