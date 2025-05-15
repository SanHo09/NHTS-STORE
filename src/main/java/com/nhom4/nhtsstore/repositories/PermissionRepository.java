package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionRepository extends GenericRepository<Permission,Long> {
}
