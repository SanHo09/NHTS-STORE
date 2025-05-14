package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.mappers.user.UserMapper;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.repositories.specification.UserSpecification;
import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.utils.PageResponseHelper;
import com.nhom4.nhtsstore.utils.ValidationHelper;
import com.nhom4.nhtsstore.viewmodel.user.*;
import javafx.application.Platform;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService  implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ApplicationState applicationState;
    private final ValidationHelper validationHelper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       ApplicationState applicationState,
                       ValidationHelper validationHelper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.applicationState = applicationState;
        this.validationHelper = validationHelper;
    }

    @Override
    public UserSessionVm authenticate(String username, String password) throws AuthenticationException {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        if (auth.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(auth);
            return UserMapper.toUserSessionVm((User)auth.getPrincipal());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailVm createUser(UserCreateVm userCreateVm) {
        User user = UserMapper.toModel(userCreateVm);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDetailVm(savedUser);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailVm editProfile(UserUpdateVm profileVm) {
        if (!hasUserPermission(profileVm.getUserId())) {
            throw new IllegalArgumentException("You do not have permission to edit this user");
        }

        User user = userRepository.findById(profileVm.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isSelfUser = isSelf(profileVm.getUserId());
        boolean isSuperAdminUser = isSuperAdmin();

        // Update basic profile information
        user.setEmail(profileVm.getEmail());
        user.setFullName(profileVm.getFullName());
        user.setAvatar(profileVm.getAvatar());

        // Super admin can edit other users' statuses and roles
        if (isSuperAdminUser && !isSelfUser) {
            // Update role if provided
            if (profileVm.getRole() != null) {
                Role role = new Role();
                role.setRoleId(profileVm.getRole().getRoleId());
                user.setRole(role);
            }

            // Update password if provided
            if (profileVm.getPassword() != null && !profileVm.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(profileVm.getPassword()));
            }
            user.setActive(profileVm.isActive());
        }

        User savedUser = userRepository.save(user);
        if (isSelfUser) {
            Platform.runLater(() -> {
                applicationState.updateUserSession(UserMapper.toUserSessionVm(savedUser));
            });
        }
        return UserMapper.toUserDetailVm(savedUser);
    }

    @Override
    public UserDetailVm changePassword(UserChangePasswordVm userChangePasswordVm) {
        if (!hasUserPermission(userChangePasswordVm.getUserId())) {
            throw new IllegalArgumentException("You do not have permission to change this user's password");
        }

        User user = userRepository.findById(userChangePasswordVm.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(userChangePasswordVm.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!userChangePasswordVm.getNewPassword().equals(userChangePasswordVm.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(userChangePasswordVm.getNewPassword()));
        return UserMapper.toUserDetailVm(userRepository.save(user));
    }



    @Override
    public UserDetailVm findUserById(Long userId) {
        User userSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isSelf = isSelf(userId);
        User user = userRepository.findById(isSelf ? userSession.getUserId() : userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserMapper.toUserDetailVm(user);
    }

    
    @Override
    public boolean isSelf(Long targetUserId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Objects.equals(currentUser.getUserId(), targetUserId);
    }

    @Override
    public boolean isSuperAdmin() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getRole() != null &&
                currentUser.getRole().getRoleName().equals("SUPER_ADMIN");
    }

    @Override
    public boolean hasUserPermission(Long targetUserId) {
        return isSelf(targetUserId) || isSuperAdmin();
    }


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }


    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User save(User entity) {
        return userRepository.save(entity);
    }


    @Override
    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Cannot delete this user because it is being used by other entities");
        }
    }

    @Override
    public void deleteMany(List<User> entities) {
        try {
            userRepository.deleteAll(entities);
        }catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Cannot delete these users because they are being used by other entities");
        }
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return userRepository.findAll(pageable);
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;  // Return the actual repository
    }


}