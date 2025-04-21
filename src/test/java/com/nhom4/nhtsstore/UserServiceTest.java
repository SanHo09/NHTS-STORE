package com.nhom4.nhtsstore;
import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.common.UserStatus;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.entities.rbac.UserHasRole;
import com.nhom4.nhtsstore.mappers.user.IUserCreateMapper;
import com.nhom4.nhtsstore.mappers.user.IUserMapper;
import com.nhom4.nhtsstore.mappers.user.IUserUpdateMapper;
import com.nhom4.nhtsstore.repositories.RoleRepository;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.repositories.specification.SearchOperation;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.utils.PageResponseHelper;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import com.nhom4.nhtsstore.viewmodel.user.UserCreateVm;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

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



    @Test
    public void testSearchUsers_Equality() {
        testSearchUsers(SearchOperation.EQUALITY, "phamduyhuy", "phamduyhuy");
    }

    @Test
    public void testSearchUsers_Like() {
        testSearchUsers(SearchOperation.LIKE, "pham%", "phamduyhuy");
    }

    @Test
    public void testSearchUsers_StartsWith() {
        testSearchUsers(SearchOperation.STARTS_WITH, "pham%", "phamduyhuy");
    }

    @Test
    public void testSearchUsers_EndsWith() {
        testSearchUsers(SearchOperation.ENDS_WITH, "%huy", "phamduyhuy");
    }

    @Test
    public void testSearchUsers_Contains() {
        testSearchUsers(SearchOperation.CONTAINS, "%duy%", "phamduyhuy");
    }

    private void testSearchUsers(SearchOperation operation, String searchValue, String expectedUsername) {
        // Arrange
        SpecSearchCriteria criteria = new SpecSearchCriteria("username", operation, searchValue);
        int page = 0;
        int size = 10;
        String sortBy = "userId";
        String sortDir = "asc";

        Pageable pageable = PageResponseHelper.createPageable(page, size, sortBy, sortDir);
        User user = new User();
        user.setUserId(1);
        user.setUsername("phamduyhuy");

        UserRecordVm userRecordVm = new UserRecordVm();
        userRecordVm.setUserId(1);
        userRecordVm.setUsername("phamduyhuy");

        List<User> userList = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toVm(any(User.class))).thenReturn(userRecordVm);

        // Act
        PageResponse<UserRecordVm> result = userService.searchUsers(criteria, page, size, sortBy, sortDir);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(expectedUsername, result.getContent().get(0).getUsername());
    }

    @Test
    public void testCreateUser() {
        // Arrange
        UserCreateVm userCreateVm = UserCreateVm.builder()
                .username("testUser")
                .password("password")
                .email("testUser@email.com")
                .status(UserStatus.ACTIVE)
                .fullName("Test User")
                .build();

        User user = new User();
        user.setUserId(1);
        user.setUsername("testUser");
        user.setEmail("testUser@email.com");
        user.setFullName("Test User");

        UserDetailVm userDetailVm = UserDetailVm.builder()
                .userId(1)
                .username("testUser")
                .email("testUser@email.com")
                .fullName("Test User")
                .status(UserStatus.ACTIVE)
                .roles(Collections.emptySet())
                .build();

        when(userCreateUpdateMapper.toModel(userCreateVm)).thenReturn(user);
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDetailVm(any(User.class))).thenReturn(userDetailVm);

        // Act
        UserDetailVm result = userService.createUser(userCreateVm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("testUser", result.getUsername());
        assertEquals("testUser@email.com", result.getEmail());

        verify(userCreateUpdateMapper).toModel(userCreateVm);
//        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDetailVm(any(User.class));
    }
    @Test
    public void testCreateUserWithNonExistingRole() {
        // Arrange
        Set<RoleVm> roleVms = Set.of(RoleVm.builder()
                .roleId(999) // Non-existing role ID
                .roleName("NON_EXISTING_ROLE")
                .description("Role that doesn't exist")
                .build());

        UserCreateVm userCreateVm = UserCreateVm.builder()
                .username("testUser")
                .password("password")
                .email("testUser@email.com")
                .status(UserStatus.ACTIVE)
                .fullName("testUser")
                .roles(roleVms)
                .build();

        // Create user that would be returned by mapper
        User user = new User();
        user.setUserId(1);
        user.setUsername("testUser");
        // Add role information to the user
        Set<UserHasRole> userRoles = mock(Set.class);
        user.setRoles(userRoles);

        // Create result to return
        UserDetailVm userDetailVm = UserDetailVm.builder()
                .userId(1)
                .username("testUser")
                .roles(Set.of(RoleWithPermissionVm.builder()
                        .id(999)
                        .roleName("NON_EXISTING_ROLE")
                        .permissions(Collections.emptySet())
                        .build()))
                .build();

        // Mock the mapper behavior
        when(userCreateUpdateMapper.toModel(userCreateVm)).thenReturn(user);
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDetailVm(any(User.class))).thenReturn(userDetailVm);

        // Act
        UserDetailVm result = userService.createUser(userCreateVm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("testUser", result.getUsername());
        assertEquals(1, result.getRoles().size());
        assertEquals("NON_EXISTING_ROLE", result.getRoles().iterator().next().getRoleName());

        verify(userCreateUpdateMapper).toModel(userCreateVm);
//        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDetailVm(any(User.class));
    }

    @Test
    public void testDeleteUser_SetInactive() {
        // Arrange
        int userId = 1;
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doThrow(new DataIntegrityViolationException("Data integrity violation")).when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        assertEquals(UserStatus.INACTIVE, existingUser.getStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteUser_DeleteCompletely() {
        // Arrange
        int userId = 1;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        UserUpdateVm userUpdateVm = new UserUpdateVm();
        userUpdateVm.setUserId(1);

        User existingUser = new User();
        existingUser.setUserId(1);
        existingUser.setUsername("oldUsername");

        User updatedUser = new User();
        updatedUser.setUserId(1);
        updatedUser.setUsername("newUsername");

        UserRecordVm userRecordVm = new UserRecordVm();
        userRecordVm.setUserId(1);
        userRecordVm.setUsername("newUsername");

        when(userRepository.findById(userUpdateVm.getUserId())).thenReturn(Optional.of(existingUser));
        when(userUpdateMapper.toModel(eq(userUpdateVm))).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toVm(updatedUser)).thenReturn(userRecordVm);

        // Act
        UserRecordVm result = userService.updateUser(userUpdateVm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("newUsername", result.getUsername());

        verify(userRepository, times(1)).findById(userUpdateVm.getUserId());
        verify(userUpdateMapper, times(1)).toModel(eq(userUpdateVm));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toVm(updatedUser);
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

            UserUpdateVm profileVm = new UserUpdateVm();
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
            UserDetailVm result = userService.editProfile(profileVm);

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

            UserUpdateVm profileVm = new UserUpdateVm();
            profileVm.setUserId(1);
            profileVm.setPassword(wrongPassword);
            profileVm.setNewPassword("newPassword");
            profileVm.setConfirmPassword("newPassword");

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(wrongPassword, encodedCurrentPassword)).thenReturn(false);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> userService.editProfile(profileVm));

            verify(passwordEncoder).matches(wrongPassword, encodedCurrentPassword);
            verify(userRepository, never()).save(any(User.class));
        }
    }
}