package com.nhom4.nhtsstore.viewmodel.user;

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
    int id;
    String username;
    String email;
    String fullName;
    String password;
    String newPassword;
    String confirmPassword;
    boolean isActive;
}
