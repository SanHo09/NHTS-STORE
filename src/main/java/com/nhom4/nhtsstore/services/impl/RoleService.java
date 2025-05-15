package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.RoleHasPermission;
import com.nhom4.nhtsstore.repositories.RoleRepository;
import com.nhom4.nhtsstore.services.IRoleService;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class RoleService implements IRoleService {
    @Autowired
    private RoleRepository repository;

    @Override
    public Set<RoleVm> getAllRoles() {
        return repository.findAll()
                .stream()
                .map(role -> RoleVm.builder().roleId(role.getRoleId()).roleName(role.getRoleName()).build())
                .collect(Collectors.toSet());
    }

    @Override
    public List<Role> findAll() {
        return repository.findAll();
    }

    @Override
    public Role findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Role save(Role entity) {
        if(entity.getRoleId() != null) {
            Role role = repository.findById(entity.getRoleId()).orElse(null);
            if(role == null) return null;
            role.setRoleName(entity.getRoleName());
            role.setDescription(entity.getDescription());
            role.setActive(entity.isActive());
            Set<RoleHasPermission> roleHasPermissions = new HashSet<>();
            if(entity.getRolePermissions() != null){
                for (RoleHasPermission roleHasPermission : entity.getRolePermissions()) {
                    RoleHasPermission roleHasPermission1 = new RoleHasPermission();
                    Permission permission = new Permission();
                    permission.setPermissionId(roleHasPermission.getPermission().getPermissionId());
                    roleHasPermission1.setRole(Role.builder().roleId(role.getRoleId()).build());
                    roleHasPermission1.setPermission(permission);
                    roleHasPermissions.add(roleHasPermission1);
                }
            }
            role.setRolePermissions(roleHasPermissions);
            return repository.save(role);
        }
        return repository.save(entity);
    }
    
    @Override
    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Cannot delete this role because it is being used by other users");
        }
    }

    @Override
    public void deleteMany(List<Role> entities) {
        try {
            repository.deleteAll(entities);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Cannot delete these roles because they are being used by other users");
        }
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(pageable);
    }

    @Override
    public RoleRepository getRepository() {
        return repository;
    }

    @Override
    public Page<Role> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Role> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Role> keywordSpec = Specification.where(null);
            for (String field : searchFields) {
                keywordSpec = keywordSpec.or((root, query, cb) -> 
                    cb.like(cb.lower(root.get(field)), "%" + keyword.toLowerCase() + "%"));
            }
            spec = spec.and(keywordSpec);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(spec, pageable);
    }
}
