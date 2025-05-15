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
    Long userId;
    String username;
    String fullName;
    String email;
    byte[] avatar;
    String role;
    Set<String> permissions;
    boolean active;
}
