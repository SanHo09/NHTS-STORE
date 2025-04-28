package com.nhom4.nhtsstore.viewmodel.user;

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

}
