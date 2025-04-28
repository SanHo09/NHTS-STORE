package com.nhom4.nhtsstore;

import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.mappers.user.IUserCreateMapper;
import com.nhom4.nhtsstore.mappers.user.IUserMapper;
import com.nhom4.nhtsstore.mappers.user.IUserUpdateMapper;
import com.nhom4.nhtsstore.repositories.RoleRepository;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.viewmodel.user.UserChangePasswordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class UserEditAndChangePasswordTest {
    private static final Logger logger = LoggerFactory.getLogger(UserEditAndChangePasswordTest.class);

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
    @BeforeEach
    public void setupUserSessionMock() {
        UserSessionVm userSessionVm = new UserSessionVm();
        userSessionVm.setUserId(1L);
        userSessionVm.setUsername("testUser");
        userSessionVm.setRoles(Set.of("SUPER_ADMIN"));
        lenient().when(applicationState.getCurrentUser()).thenReturn(userSessionVm);
    }
}


