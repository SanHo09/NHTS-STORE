package com.nhom4.nhtsstore.viewmodel.role;

import com.nhom4.nhtsstore.viewmodel.permission.PermissionVm;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleWithPermissionVm {
    Integer id;
    String roleName;
    String description;
    Set<PermissionVm> permissions;
}
