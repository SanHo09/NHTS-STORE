package com.nhom4.nhtsstore.viewmodel.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateVm {
    Long id;
    String roleName;
    String description;
    boolean active;
    Long[] permissionIds;

}
