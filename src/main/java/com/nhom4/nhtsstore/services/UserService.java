package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.mappers.IUserCreateUpdateMapper;
import com.nhom4.nhtsstore.mappers.IUserMapper;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.utils.PageResponseHelper;
import com.nhom4.nhtsstore.viewmodel.user.*;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserMapper userMapper;
    private final IUserCreateUpdateMapper userCreateUpdateMapper;
    private final AuthenticationManager authenticationManager;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, IUserMapper userMapper, IUserCreateUpdateMapper userCreateUpdateMapper, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userCreateUpdateMapper = userCreateUpdateMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserSessionVm authenticate(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            if (auth.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(auth);
                return userMapper.toUserSessionVm(userRepository
                        .findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found")));
            }
            return null;

        } catch (AuthenticationException e) {
            return null;
        }
    }
    
    @Override
    @Transactional
    public UserRecordVm createUser(UserCreateVm userCreateVm) {
        User user = userCreateUpdateMapper.toModel(userCreateVm);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.toVm(savedUser);
    }

    @Override
    public UserRecordVm updateUser(UserUpdateVm userUpdateVm) {
        return null;
    }

    @Override
    public void deleteUser(int userId) {

        userRepository.deleteById(userId);
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
        return userRepository.findById(userId).map(userMapper::toUserDetailVm)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }




}