package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.viewmodel.user.*;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IUserService {
    // login, don't need to check permission
    @PreAuthorize("permitAll()")
    UserSessionVm authenticate(String username, String password);

    @PreAuthorize("hasAnyAuthority('USER_CREATION','FULL_ACCESS','USER_MANAGEMENT')")
    UserDetailVm createUser(UserCreateVm userCreateVm);

    @PreAuthorize("hasAnyAuthority('USER_UPDATE','FULL_ACCESS','USER_MANAGEMENT')")
    UserRecordVm updateUser(UserUpdateVm userUpdateVm);

    @PreAuthorize("hasAnyAuthority('USER_DELETION','FULL_ACCESS','USER_MANAGEMENT')")
    void deleteUser(int userId);


    UserDetailVm editProfile(UserUpdateVm profileVm);
    @PreAuthorize("hasAnyAuthority('FULL_ACCESS','USER_MANAGEMENT')")
    UserDetailVm changePassword(UserChangePasswordVm profileVm);

    @PreAuthorize("hasAnyAuthority('USER_LIST','FULL_ACCESS','USER_MANAGEMENT')")
    PageResponse<UserRecordVm> findAllUsers(int page, int size, String sortBy, String sortDir);

    @PreAuthorize("hasAnyAuthority('USER_DETAIL','FULL_ACCESS','USER_MANAGEMENT') or #userId == applicationState.currentUser.userId")
    UserDetailVm findUserById(int userId);

    @PreAuthorize("hasAnyAuthority('USER_LIST','FULL_ACCESS','USER_MANAGEMENT')")
    PageResponse<UserRecordVm> searchUsers(SpecSearchCriteria criteria, int page, int size, String sortBy, String sortDir);
}
