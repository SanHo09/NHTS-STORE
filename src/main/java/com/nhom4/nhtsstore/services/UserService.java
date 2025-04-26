package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.common.UserStatus;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.entities.rbac.UserHasRole;
import com.nhom4.nhtsstore.mappers.user.IUserCreateMapper;
import com.nhom4.nhtsstore.mappers.user.IUserMapper;
import com.nhom4.nhtsstore.mappers.user.IUserUpdateMapper;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.repositories.specification.UserSpecification;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.utils.PageResponseHelper;
import com.nhom4.nhtsstore.utils.ValidationHelper;
import com.nhom4.nhtsstore.viewmodel.user.*;
import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserMapper userMapper;
    private final IUserCreateMapper userCreateUpdateMapper;
    private final IUserUpdateMapper userUpdateMapper;
    private final AuthenticationManager authenticationManager;
    private final ApplicationState applicationState;
    private final ValidationHelper validationHelper;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, IUserMapper userMapper, IUserCreateMapper userCreateUpdateMapper, IUserUpdateMapper userUpdateMapper, AuthenticationManager authenticationManager, ApplicationState applicationState, ValidationHelper validationHelper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userCreateUpdateMapper = userCreateUpdateMapper;
        this.userUpdateMapper = userUpdateMapper;
        this.authenticationManager = authenticationManager;
        this.applicationState = applicationState;
        this.validationHelper = validationHelper;
    }

    @Override
    public UserSessionVm authenticate(String username, String password) throws AuthenticationException{
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        if (auth.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(auth);
            return userMapper.toUserSessionVm((User)auth.getPrincipal());
        }
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailVm createUser(UserCreateVm userCreateVm) {
        User user = userCreateUpdateMapper.toModel(userCreateVm);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserDetailVm(savedUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRecordVm updateUser(UserUpdateVm userUpdateVm) {
        User existingUser = userRepository.findById(userUpdateVm.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Map the update VM to the existing user entity
        User updatedUser = userUpdateMapper.toModel(userUpdateVm);

        // Save the updated user
        User savedUser = userRepository.save(updatedUser);

        return userMapper.toVm(savedUser);
    }
    @Override
    public void deleteUser(int userId) {
        try {
            userRepository.deleteById(userId);
        } catch (DataAccessException e) {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            existingUser.setStatus(UserStatus.INACTIVE);
            userRepository.save(existingUser);
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailVm editProfile(UserUpdateVm profileVm) {
        User userSession= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userSession.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserSessionVm userSessionVm = applicationState.getCurrentUser();
        boolean isSelf = Objects.equals(userSessionVm.getUserId(), profileVm.getUserId());
        boolean isSuperAdmin = userSessionVm.getRoles().stream()
                .anyMatch(role -> role.equals("SUPER_ADMIN"));
        if (!isSelf && !isSuperAdmin) {
            throw new IllegalArgumentException("You do not have permission to edit this user");
        }
        if (isSuperAdmin && !Objects.equals(userSessionVm.getUserId(), profileVm.getUserId())) {
            if (profileVm.getStatus() != null) {
                user.setStatus(profileVm.getStatus());
            }
            if (profileVm.getRoles() != null && !profileVm.getRoles().isEmpty()) {
                Set<Role> roles = profileVm.getRoles().stream()
                        .map(roleVm -> Role.builder()
                                .roleId(roleVm.getRoleId())
                                .roleName(roleVm.getRoleName())
                                .description(roleVm.getDescription())
                                .build())
                        .collect(Collectors.toSet());
                Set<UserHasRole> userHasRoles = roles.stream()
                        .map(role -> UserHasRole.builder()
                                .role(role)
                                .user(user)
                                .build())
                        .collect(Collectors.toSet());
                user.setRoles(userHasRoles);
            }
        }
        user.setAvatar(profileVm.getAvatar());
        user.setEmail(profileVm.getEmail());
        user.setFullName(profileVm.getFullName());
        User savedUser = userRepository.save(user);
        return userMapper.toUserDetailVm(savedUser);
    }

    @Override
    public UserDetailVm changePassword( UserChangePasswordVm userChangePasswordVm) {

        User userSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userSession.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(userChangePasswordVm.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!userChangePasswordVm.getNewPassword().equals(userChangePasswordVm.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(userChangePasswordVm.getNewPassword()));
        return userMapper.toUserDetailVm(userRepository.save(user));
    }

    @Override
    public PageResponse<UserRecordVm> findAllUsers(int page, int size, String sortBy, String sortDir) {

        Page<UserRecordVm> userPage = userRepository
                .findAll(PageResponseHelper.createPageable(page,size,sortBy,sortDir))
                .map(userMapper::toVm);
        return PageResponseHelper.createPageResponse(userPage);
    }


    @Override
    public UserDetailVm findUserById(int userId) {
        UserSessionVm userSessionVm = applicationState.getCurrentUser();
        boolean isSelf = userSessionVm.getUserId() == userId;
        boolean isSuperAdmin = userSessionVm.getRoles().stream()
                .anyMatch(role -> role.equals("SUPER_ADMIN"));

        if (!isSelf && !isSuperAdmin) {
            throw new IllegalArgumentException("You do not have permission to view this user");
        }
        User user = userRepository.findById(isSelf ? userSessionVm.getUserId() : userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return userMapper.toUserDetailVm(user);

    }


    @Override
    public PageResponse<UserRecordVm> searchUsers(SpecSearchCriteria criteria, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageResponseHelper.createPageable(page, size, sortBy, sortDir);
        Specification<User> spec = new UserSpecification(criteria);
        Page<User> userPage = userRepository.findAll(spec, pageable);
        Page<UserRecordVm> userRecordPage = userPage.map(userMapper::toVm);
        return PageResponseHelper.createPageResponse(userRecordPage);
    }


}