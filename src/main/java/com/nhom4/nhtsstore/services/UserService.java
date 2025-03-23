package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Role;
import com.nhom4.nhtsstore.entities.User;
import com.nhom4.nhtsstore.mappers.IUserMapper;
import com.nhom4.nhtsstore.repositories.PermissionRepository;
import com.nhom4.nhtsstore.repositories.RoleRepository;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.viewmodel.user.UserCreateVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service

public class UserService implements IUserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserMapper userMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder, IUserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public boolean authenticate(String username, String password) {
        try {
            UserDetails userDetails = loadUserByUsername(username);
            return passwordEncoder.matches(password, userDetails.getPassword());
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    @Override
    public UserSessionVm findByUsername(String username) {
        return userMapper.toUserSessionVm(userRepository.findByUsernameWithRolesAndPermissions(username).orElse(null));

    }

    @Override
    @Transactional
    public UserRecordVm createUser(UserCreateVm userCreateVm) {
        // Convert UserCreateVm to User entity
        User user = userMapper.toUser(userCreateVm);

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Retrieve roles from the database instead of creating new ones
        Set<Role> roles = roleRepository.findByRoleNameIn(userCreateVm.getRoles());
        user.setRoles(roles);

        // Save User entity
        User savedUser = userRepository.save(user);

        // Convert User entity to UserRecordVm
        return userMapper.toUserRecord(savedUser);
    }

    @Override
    public UserRecordVm updateUser(UserUpdateVm userUpdateVm) {
        return null;
    }

    @Override
    public void deleteUser(int userId) {

    }


    @Override
    public Page<UserRecordVm> findUsersPage(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserRecord);
    }

    @Override
    public UserRecordVm findUserRecordById(int userId) {
        return null;
    }

    @Override
    public Page<UserRecordVm> filterUser(String keyword, Pageable pageable) {
        return null;
    }


}
