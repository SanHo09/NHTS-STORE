package com.nhom4.nhtsstore.viewmodel.permission;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionVm {
    Long id;
    String permissionName;
    String description;



}
