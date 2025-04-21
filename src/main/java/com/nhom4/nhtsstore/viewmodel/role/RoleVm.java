package com.nhom4.nhtsstore.viewmodel.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleVm {
    Integer roleId;
    String roleName;
    String description;
}
