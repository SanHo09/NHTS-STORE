package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByPermissionName(String permissionName);
}