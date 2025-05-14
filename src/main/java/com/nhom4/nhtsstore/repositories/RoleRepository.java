package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.rbac.Role;


import java.util.Optional;

public interface RoleRepository extends GenericRepository<Role,Long> {
    Optional<Role> findByRoleName(String roleName);
}
