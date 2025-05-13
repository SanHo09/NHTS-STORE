package com.nhom4.nhtsstore.viewmodel.user;

import com.nhom4.nhtsstore.entities.rbac.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRecordVm {
    Long userId;
    String username;
    String email;
    String fullName;
    byte[] avatar;
    boolean active;
    String role;

    public static UserRecordVm fromModelToVm(User user) {
        return UserRecordVm.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .active(user.isActive())
                .role(user.getRole().getRoleName())
                .build();
    }
}
