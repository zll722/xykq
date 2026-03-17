package com.campus.attendance.service;

import com.campus.attendance.dto.auth.ChangePasswordRequest;
import com.campus.attendance.dto.auth.LoginRequest;
import com.campus.attendance.dto.auth.LoginResponse;
import com.campus.attendance.dto.auth.RegisterRequest;

import java.util.List;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    LoginResponse.UserInfo me(Long userId);

    List<String> myPermissions(Long userId);
}
