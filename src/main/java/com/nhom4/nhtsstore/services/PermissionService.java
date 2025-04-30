package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.repositories.PermissionRepository;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PermissionService implements GenericService<Permission> {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission findById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    @Override
    public Permission save(Permission entity) {
        return permissionRepository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        try {
            permissionRepository.deleteById(id);
        }catch (InvalidDataAccessApiUsageException ex){
            throw new InvalidDataAccessApiUsageException("Cannot delete this permission because it is being used by other entities");
        }

    }

    @Override
    public void deleteMany(List<Permission> entities) {
        try {
            permissionRepository.deleteAll(entities);
        }catch (InvalidDataAccessApiUsageException ex){
            throw new InvalidDataAccessApiUsageException("Cannot delete these permissions because they are being used by other entities");
        }

    }

    @Override
    public Page<Permission> findAll(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    @Override
    public Page<Permission> search(String keyword, List<String> searchFields, Pageable pageable) {
        return null;
    }
}
