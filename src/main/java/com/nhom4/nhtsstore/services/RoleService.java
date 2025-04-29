package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.repositories.RoleRepository;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService,GenericService<Role> {
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
        return null;
    }

    @Override

    public void deleteById(Long id) {
        try {
            roleRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            List<User> users = userRepository.findByRole(findById(id));
            if(!users.isEmpty()){
                users.forEach(user -> {
                    user.setRole(null);
                });
            }
            userRepository.saveAll(users);
            roleRepository.delete(findById(id));

        }
    }

    @Override
    public void deleteMany(List<Role> entities) {
        try {
            roleRepository.deleteAll(entities);
        }catch (DataIntegrityViolationException e){
            List<User> users = userRepository.findByRoleIn(entities);
            if(!users.isEmpty()){
                users.forEach(user -> {
                    user.setRole(null);
                });
            }
            userRepository.saveAll(users);
            roleRepository.deleteAll(entities);
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
