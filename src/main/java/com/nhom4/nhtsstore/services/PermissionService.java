package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.repositories.PermissionRepository;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
@Service
public class PermissionService implements GenericService<Permission> {
    @Autowired
    private PermissionRepository permissionRepository;
    
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
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return permissionRepository.findAll(pageable);
    }

    @Override
    public Page<Permission> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Permission> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Permission> keywordSpec = Specification.where(null);
            for (String field : searchFields) {
                keywordSpec = keywordSpec.or((root, query, cb) -> 
                    cb.like(cb.lower(root.get(field)), "%" + keyword.toLowerCase() + "%"));
            }
            spec = spec.and(keywordSpec);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return permissionRepository.findAll(spec, pageable);
    }
}
