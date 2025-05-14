package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.repositories.RoleRepository;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;

import java.util.Set;

public interface IRoleService extends GenericService<Role, Long, RoleRepository> {
    Set<RoleVm> getAllRoles();

}
