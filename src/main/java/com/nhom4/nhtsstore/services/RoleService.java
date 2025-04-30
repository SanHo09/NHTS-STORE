package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.RoleHasPermission;
import com.nhom4.nhtsstore.repositories.RoleRepository;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService, GenericService<Role> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Set<RoleVm> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(role -> RoleVm.builder().roleId(role.getRoleId()).roleName(role.getRoleName()).build())
                .collect(Collectors.toSet());
    }

    @Override
    public List<Role> findAll() {
        return List.of();
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role save(Role entity) {
        if(entity.getRoleId() != null) {
            Role role = roleRepository.findById(entity.getRoleId()).orElse(null);
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
            return roleRepository.save(role);
        }
        return roleRepository.save(entity);
    }
//    public RoleVm update(RoleUpdateVm updateVm){
//        Role role = roleRepository.findById(updateVm.getId()).orElse(null);
//        if(role == null) return null;
//        role.setRoleName(updateVm.getRoleName());
//        role.setDescription(updateVm.getDescription());
//        role.setActive(updateVm.isActive());
//        Set<RoleHasPermission> roleHasPermissions = new HashSet<>();
//        if(updateVm.getPermissionIds() != null){
//            for (Long permissionId : updateVm.getPermissionIds()) {
//                RoleHasPermission roleHasPermission = new RoleHasPermission();
//                Permission permission = new Permission();
//                permission.setPermissionId(permissionId);
//                roleHasPermission.setRole(role);
//                roleHasPermission.setPermission(permission);
//                roleHasPermissions.add(roleHasPermission);
//            }
//        }
//        role.setRolePermissions(roleHasPermissions);
//        role = roleRepository.save(role);
//        return RoleVm.builder()
//                .roleId(role.getRoleId())
//                .roleName(role.getRoleName())
//                .build();
//    }

    @Override
    public void deleteById(Long id) {
        try {
            roleRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
//            List<User> users = userRepository.findByRole(findById(id));
//            if(!users.isEmpty()){
//                users.forEach(user -> {
//                    user.setRole(null);
//                });
//            }
//            userRepository.saveAll(users);
//            roleRepository.delete(findById(id));
            throw new DataIntegrityViolationException("Cannot delete this role because it is being used by other users");

        }
    }

    @Override
    public void deleteMany(List<Role> entities) {
        try {
            roleRepository.deleteAll(entities);
        }catch (DataIntegrityViolationException e){
//            List<User> users = userRepository.findByRoleIn(entities);
//            if(!users.isEmpty()){
//                users.forEach(user -> {
//                    user.setRole(null);
//                });
//            }
//            userRepository.saveAll(users);
//            roleRepository.deleteAll(entities);
            throw new DataIntegrityViolationException("Cannot delete these roles because they are being used by other users");
        }
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    @Override
    public Page<Role> search(String keyword, List<String> searchFields, Pageable pageable) {
        return null;
    }
}
