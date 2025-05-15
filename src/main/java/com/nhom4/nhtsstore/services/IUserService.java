package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.viewmodel.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IUserService extends GenericService<User, Long, UserRepository>{
    // login, don't need to check permission
    @PreAuthorize("permitAll()")
    UserSessionVm authenticate(String username, String password);

    @PreAuthorize("hasAnyAuthority('USER_CREATION','FULL_ACCESS','USER_MANAGEMENT')")
    UserDetailVm createUser(UserCreateVm userCreateVm);

    UserDetailVm editProfile(UserUpdateVm profileVm);

    UserDetailVm changePassword(UserChangePasswordVm profileVm);


    @PreAuthorize("hasAnyAuthority('USER_DETAIL','FULL_ACCESS','USER_MANAGEMENT') or @userService.hasUserPermission(#userId) or @userService.isSuperAdmin()")
    UserDetailVm findUserById(Long userId);


    @Override
    @PreAuthorize("hasAnyAuthority('USER_LIST','FULL_ACCESS','USER_MANAGEMENT')")
    List<User> findAll();
    @Override
    @PreAuthorize("hasAnyAuthority('USER_LIST','FULL_ACCESS','USER_MANAGEMENT')")
    User findById(Long id);
    @Override
    @PreAuthorize("hasAnyAuthority('USER_LIST','FULL_ACCESS','USER_MANAGEMENT')")
    User save(User entity);
    @Override
    @PreAuthorize("hasAnyAuthority('USER_LIST','FULL_ACCESS','USER_MANAGEMENT')")
    void deleteById(Long id);
    @Override
    @PreAuthorize("hasAnyAuthority('USER_LIST','FULL_ACCESS','USER_MANAGEMENT')")
    void deleteMany(List<User> entities);




    boolean isSelf(Long targetUserId);
    boolean isSuperAdmin();
    boolean hasUserPermission(Long targetUserId);
}
