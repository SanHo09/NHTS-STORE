package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.viewmodel.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IUserService {
    // login
    UserSessionVm authenticate(String username, String password);
    
    UserRecordVm createUser(UserCreateVm userCreateVm);
    UserRecordVm updateUser(UserUpdateVm userUpdateVm);
    void deleteUser(int userId);
    PageResponse<UserRecordVm> findAllUsers(int page, int size, String sortBy, String sortDir);
    UserDetailVm findUserById(int userId);

}
