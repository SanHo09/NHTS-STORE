package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.viewmodel.user.*;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IUserService {
    // login
    UserSessionVm authenticate(String username, String password);

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    UserDetailVm createUser(UserCreateVm userCreateVm);

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    UserRecordVm updateUser(UserUpdateVm userUpdateVm);

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    void deleteUser(int userId);

    @PreAuthorize("#profileVm.userId == authentication.principal.userId")
    UserDetailVm editProfile(UserUpdateVm profileVm);

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    PageResponse<UserRecordVm> findAllUsers(int page, int size, String sortBy, String sortDir);

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    UserDetailVm findUserById(int userId);

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    PageResponse<UserRecordVm> searchUsers(SpecSearchCriteria criteria, int page, int size, String sortBy, String sortDir);
}
