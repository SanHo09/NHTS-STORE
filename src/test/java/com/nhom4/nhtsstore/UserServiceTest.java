package com.nhom4.nhtsstore;
import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.common.UserStatus;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.mappers.user.IUserCreateMapper;
import com.nhom4.nhtsstore.mappers.user.IUserMapper;
import com.nhom4.nhtsstore.mappers.user.IUserUpdateMapper;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.repositories.specification.SearchOperation;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.utils.PageResponseHelper;
import com.nhom4.nhtsstore.viewmodel.user.UserCreateVm;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
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
        UserCreateVm userCreateVm = new UserCreateVm();
        userCreateVm.setUsername("testUser");
        userCreateVm.setPassword("password");

        User user = new User();
        user.setUsername("testUser");
        user.setUserId(1);

        UserRecordVm userRecordVm = new UserRecordVm();
        userRecordVm.setUserId(1);
        userRecordVm.setUsername("testUser");

        when(userCreateUpdateMapper.toModel(userCreateVm)).thenReturn(user);
//        when(passwordEncoder.encode(userCreateVm.getPassword())).thenReturn(passwordEncoder.encode(userCreateVm.getPassword()));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toVm(user)).thenReturn(userRecordVm);

        // Act
        UserRecordVm result = userService.createUser(userCreateVm);

        // Assert
        assertEquals(1, result.getUserId());
        assertEquals("testUser", result.getUsername());

        verify(userCreateUpdateMapper, times(1)).toModel(userCreateVm);
//        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toVm(user);
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
}