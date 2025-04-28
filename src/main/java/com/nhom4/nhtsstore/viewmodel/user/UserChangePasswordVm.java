package com.nhom4.nhtsstore.viewmodel.user;

import jakarta.validation.constraints.NotBlank;
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
public class UserChangePasswordVm {
    Long userId;
    @NotBlank(message = "Current password is required")
    String password;
    @NotBlank(message = "New password is required")
    String newPassword;
    @NotBlank(message = "Confirm password is required")
    String confirmPassword;
}
