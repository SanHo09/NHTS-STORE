package com.nhom4.nhtsstore;


import com.nhom4.nhtsstore.mappers.user.IUserCreateMapper;
import com.nhom4.nhtsstore.mappers.user.IUserMapper;
import com.nhom4.nhtsstore.mappers.user.IUserUpdateMapper;
import com.nhom4.nhtsstore.repositories.RoleRepository;
import com.nhom4.nhtsstore.repositories.UserRepository;

import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.ApplicationState;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceCrudTest {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceCrudTest.class);

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IUserMapper userMapper;
    @Mock
    private IUserCreateMapper userCreateUpdateMapper;
    @Mock
    private IUserUpdateMapper userUpdateMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserService userService;
    @Mock
    private ApplicationState applicationState;



}
