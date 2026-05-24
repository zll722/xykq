package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.ClassInfo;
import com.campus.attendance.dto.auth.ChangePasswordRequest;
import com.campus.attendance.dto.auth.LoginRequest;
import com.campus.attendance.dto.auth.LoginResponse;
import com.campus.attendance.dto.auth.RegisterRequest;
import com.campus.attendance.mapper.ClassMapper;
import com.campus.attendance.service.AuthService;
import com.campus.attendance.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final ClassMapper classMapper;

    public AuthController(AuthService authService, ClassMapper classMapper) {
        this.authService = authService;
        this.classMapper = classMapper;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.ok(null);
    }

    @GetMapping("/me")
    public ApiResponse<LoginResponse.UserInfo> me() {
        return ApiResponse.ok(authService.me(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/permissions")
    public ApiResponse<List<String>> myPermissions() {
        return ApiResponse.ok(authService.myPermissions(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/classes")
    public ApiResponse<List<ClassInfo>> listClasses() {
        return ApiResponse.ok(classMapper.list(null, 1));
    }
}
