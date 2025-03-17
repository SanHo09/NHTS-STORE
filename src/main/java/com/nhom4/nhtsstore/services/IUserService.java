package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;

public interface IUserService {
    // login
    boolean authenticate(String username, String password);
    // find user by username
    UserSessionVm findByUsername(String username);
}
