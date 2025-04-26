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
        userSessionVm.setUserId(1);
        userSessionVm.setUsername("testUser");
        userSessionVm.setRoles(Set.of("SUPER_ADMIN"));
        lenient().when(applicationState.getCurrentUser()).thenReturn(userSessionVm);
    }

    @Test
    public void editProfileWithoutPasswordChange() {
        // Arrange
        String username = "testUser";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            // Setup existing user
            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setEmail("old@example.com");
            existingUser.setFullName("Old Name");

            // Setup profile update request
            UserUpdateVm profileVm = new UserUpdateVm();
            profileVm.setUserId(1);
            profileVm.setEmail("new@example.com");
            profileVm.setFullName("New Name");
            profileVm.setAvatar("new-avatar.jpg");

            // Setup return values
            UserDetailVm userDetailVm = UserDetailVm.builder()
                    .userId(1)
                    .username(username)
                    .email("new@example.com")
                    .fullName("New Name")
                    .avatar("new-avatar.jpg")
                    .build();
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            when(userMapper.toUserDetailVm(any(User.class))).thenReturn(userDetailVm);

            // Act
            UserDetailVm result = userService.editProfile(profileVm);

            // Assert
            assertNotNull(result);
            assertEquals("new@example.com", result.getEmail());
            assertEquals("New Name", result.getFullName());
            assertEquals("new-avatar.jpg", result.getAvatar());

            verify(userRepository).findByUsername(username);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    public void editProfileWithPasswordChange() {
        // Arrange
        String username = "testUser";
        String currentPassword = "currentPassword";
        String encodedCurrentPassword = "encodedCurrentPassword";
        String newPassword = "newPassword";
        UserSessionVm userSessionVm = new UserSessionVm();
        userSessionVm.setUserId(1);
        userSessionVm.setUsername(username);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();
            profileVm.setUserId(1);
            profileVm.setPassword(currentPassword);
            profileVm.setNewPassword(newPassword);
            profileVm.setConfirmPassword(newPassword);

            UserDetailVm userDetailVm = UserDetailVm.builder().userId(1).username(username).build();

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            when(userMapper.toUserDetailVm(any(User.class))).thenReturn(userDetailVm);

            // Act
            UserDetailVm result = userService.changePassword(profileVm);

            // Assert
            verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword);
            verify(passwordEncoder).encode(newPassword);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    public void editProfileWithIncorrectPassword() {
        // Arrange
        String username = "testUser";
        String wrongPassword = "wrongPassword";
        String encodedCurrentPassword = "encodedCurrentPassword";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();
            profileVm.setUserId(1);
            profileVm.setPassword(wrongPassword);
            profileVm.setNewPassword("newPassword");
            profileVm.setConfirmPassword("newPassword");

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(wrongPassword, encodedCurrentPassword)).thenReturn(false);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> userService.changePassword(profileVm));

            verify(passwordEncoder).matches(wrongPassword, encodedCurrentPassword);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void editProfile_updatesUserSuccessfully_whenNoPasswordChangeRequested() {
        String username = "testUser";
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setEmail("old@example.com");
            existingUser.setFullName("Old Name");
            existingUser.setAvatar("old-avatar.jpg");

            UserUpdateVm profileVm = new UserUpdateVm();
            profileVm.setUserId(1);
            profileVm.setEmail("new@example.com");
            profileVm.setFullName("New Name");
            profileVm.setAvatar("new-avatar.jpg");

            UserDetailVm userDetailVm = UserDetailVm.builder()
                    .userId(1)
                    .username(username)
                    .email("new@example.com")
                    .fullName("New Name")
                    .avatar("new-avatar.jpg")
                    .build();

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            when(userMapper.toUserDetailVm(any(User.class))).thenReturn(userDetailVm);

            logger.info("Starting test: editProfile_updatesUserSuccessfully_whenNoPasswordChangeRequested");
            UserDetailVm result = userService.editProfile(profileVm);

            assertNotNull(result);
            assertEquals("new@example.com", result.getEmail());
            assertEquals("New Name", result.getFullName());
            assertEquals("new-avatar.jpg", result.getAvatar());

            verify(userRepository).findByUsername(username);
            verify(userRepository).save(any(User.class));
            logger.info("Test passed: editProfile_updatesUserSuccessfully_whenNoPasswordChangeRequested");
        }
    }

    @Test
    void editProfile_updatesPasswordSuccessfully_whenCorrectPasswordProvided() {
        String username = "testUser";
        String currentPassword = "currentPassword";
        String encodedCurrentPassword = "encodedCurrentPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();
            profileVm.setUserId(1);
            profileVm.setPassword(currentPassword);
            profileVm.setNewPassword(newPassword);
            profileVm.setConfirmPassword(newPassword);

            UserDetailVm userDetailVm = UserDetailVm.builder().userId(1).username(username).build();

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            when(userMapper.toUserDetailVm(any(User.class))).thenReturn(userDetailVm);

            logger.info("Starting test: editProfile_updatesPasswordSuccessfully_whenCorrectPasswordProvided");
            userService.changePassword(profileVm);

            verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword);
            verify(passwordEncoder).encode(newPassword);
            verify(userRepository).save(any(User.class));
            assertEquals(encodedNewPassword, existingUser.getPassword());
            logger.info("Test passed: editProfile_updatesPasswordSuccessfully_whenCorrectPasswordProvided");
        }
    }

    @Test
    void editProfile_throwsException_whenIncorrectPasswordProvided() {
        String username = "testUser";
        String wrongPassword = "wrongPassword";
        String encodedCurrentPassword = "encodedCurrentPassword";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();
            profileVm.setUserId(1);
            profileVm.setPassword(wrongPassword);
            profileVm.setNewPassword("newPassword");
            profileVm.setConfirmPassword("newPassword");

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(wrongPassword, encodedCurrentPassword)).thenReturn(false);

            logger.info("Starting test: editProfile_throwsException_whenIncorrectPasswordProvided");
            assertThrows(IllegalArgumentException.class, () -> userService.changePassword(profileVm));

            verify(passwordEncoder).matches(wrongPassword, encodedCurrentPassword);
            verify(userRepository, never()).save(any(User.class));
            logger.info("Test passed: editProfile_throwsException_whenIncorrectPasswordProvided");
        }
    }

    @Test
    void editProfile_throwsException_whenNewPasswordsDoNotMatch() {
        String username = "testUser";
        String currentPassword = "currentPassword";
        String encodedCurrentPassword = "encodedCurrentPassword";
        String newPassword = "newPassword";
        String confirmPassword = "differentPassword";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();
            profileVm.setUserId(1);
            profileVm.setPassword(currentPassword);
            profileVm.setNewPassword(newPassword);
            profileVm.setConfirmPassword(confirmPassword);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);

            logger.info("Starting test: editProfile_throwsException_whenNewPasswordsDoNotMatch");
            assertThrows(IllegalArgumentException.class, () -> userService.changePassword(profileVm));

            verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword);
            verify(userRepository, never()).save(any(User.class));
            logger.info("Test passed: editProfile_throwsException_whenNewPasswordsDoNotMatch");
        }
    }

    @Test
    void changePassword_updatesPasswordSuccessfully_whenCorrectPasswordProvided() {
        String username = "testUser";
        String currentPassword = "currentPassword";
        String encodedCurrentPassword = "encodedCurrentPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();
            profileVm.setPassword(currentPassword);
            profileVm.setNewPassword(newPassword);
            profileVm.setConfirmPassword(newPassword);

            UserDetailVm userDetailVm = UserDetailVm.builder().userId(1).username(username).build();

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            when(userMapper.toUserDetailVm(any(User.class))).thenReturn(userDetailVm);

            logger.info("Starting test: changePassword_updatesPasswordSuccessfully_whenCorrectPasswordProvided");
            userService.changePassword(profileVm);

            verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword);
            verify(passwordEncoder).encode(newPassword);
            verify(userRepository).save(any(User.class));
            assertEquals(encodedNewPassword, existingUser.getPassword());
            logger.info("Test passed: changePassword_updatesPasswordSuccessfully_whenCorrectPasswordProvided");
        }
    }

    @Test
    void changePassword_throwsException_whenIncorrectPasswordProvided() {
        String username = "testUser";
        String wrongPassword = "wrongPassword";
        String encodedCurrentPassword = "encodedCurrentPassword";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();
            profileVm.setPassword(wrongPassword);
            profileVm.setNewPassword("newPassword");
            profileVm.setConfirmPassword("newPassword");

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(wrongPassword, encodedCurrentPassword)).thenReturn(false);

            logger.info("Starting test: changePassword_throwsException_whenIncorrectPasswordProvided");
            assertThrows(IllegalArgumentException.class, () -> userService.changePassword(profileVm));

            verify(passwordEncoder).matches(wrongPassword, encodedCurrentPassword);
            verify(userRepository, never()).save(any(User.class));
            logger.info("Test passed: changePassword_throwsException_whenIncorrectPasswordProvided");
        }
    }

    @Test
    void changePassword_throwsException_whenNewPasswordsDoNotMatch() {
        String username = "testUser";
        String currentPassword = "currentPassword";
        String encodedCurrentPassword = "encodedCurrentPassword";
        String newPassword = "newPassword";
        String confirmPassword = "differentPassword";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();
            profileVm.setPassword(currentPassword);
            profileVm.setNewPassword(newPassword);
            profileVm.setConfirmPassword(confirmPassword);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);

            logger.info("Starting test: changePassword_throwsException_whenNewPasswordsDoNotMatch");
            assertThrows(IllegalArgumentException.class, () -> userService.changePassword(profileVm));

            verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword);
            verify(userRepository, never()).save(any(User.class));
            logger.info("Test passed: changePassword_throwsException_whenNewPasswordsDoNotMatch");
        }
    }

    @Test
    void changePassword_doesNotUpdatePassword_whenNewPasswordFieldsAreNull() {
        String username = "testUser";
        String encodedCurrentPassword = "encodedCurrentPassword";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(authentication.getPrincipal()).thenReturn(username);

            User existingUser = new User();
            existingUser.setUserId(1);
            existingUser.setUsername(username);
            existingUser.setPassword(encodedCurrentPassword);

            UserChangePasswordVm profileVm = new UserChangePasswordVm();

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

            logger.info("Starting test: changePassword_doesNotUpdatePassword_whenNewPasswordFieldsAreNull");
            UserDetailVm userDetailVm = userService.changePassword(profileVm);

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository).save(any(User.class));
            assertEquals(encodedCurrentPassword, existingUser.getPassword());
            logger.info("Test passed: changePassword_doesNotUpdatePassword_whenNewPasswordFieldsAreNull");
        }
    }
}
