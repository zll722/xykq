package com.campus.attendance.service;

import com.campus.attendance.dto.auth.ChangePasswordRequest;
import com.campus.attendance.dto.auth.LoginRequest;
import com.campus.attendance.dto.auth.LoginResponse;
import com.campus.attendance.dto.auth.RegisterRequest;

import java.util.List;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void sendRegisterCode(String email);

    void sendResetCode(String username, String email);

    void resetPassword(String username, String email, String code, String newPassword);

    void register(RegisterRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    LoginResponse.UserInfo me(Long userId);

    List<String> myPermissions(Long userId);
}
