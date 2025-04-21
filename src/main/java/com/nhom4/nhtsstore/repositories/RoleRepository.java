package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.rbac.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByRoleName(String roleName);
}
