package com.nhom4.nhtsstore.viewmodel.user;

import com.nhom4.nhtsstore.common.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRecordVm {
    Integer userId;
    String username;
    String email;
    String fullName;
    String avatar;
    UserStatus status;

}
