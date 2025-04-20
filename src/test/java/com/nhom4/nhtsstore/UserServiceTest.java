package com.nhom4.nhtsstore;
import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.mappers.user.IUserMapper;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.repositories.specification.SearchOperation;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.utils.PageResponseHelper;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IUserMapper userMapper;

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
}