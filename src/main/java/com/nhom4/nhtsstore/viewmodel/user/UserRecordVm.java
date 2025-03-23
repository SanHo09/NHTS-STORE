package com.nhom4.nhtsstore.viewmodel.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRecordVm {
    Integer userId;
    String username;
    String email;
    String fullName;
}
