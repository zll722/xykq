package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.ClassInfo;
import com.campus.attendance.dto.auth.ChangePasswordRequest;
import com.campus.attendance.dto.auth.LoginRequest;
import com.campus.attendance.dto.auth.LoginResponse;
import com.campus.attendance.dto.auth.RegisterRequest;
import com.campus.attendance.exception.BizException;
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
import java.util.Map;

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

    @PostMapping("/send-register-code")
    public ApiResponse<Void> sendRegisterCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            throw new BizException(4000, "邮箱不能为空");
        }
        authService.sendRegisterCode(email);
        return ApiResponse.ok(null);
    }

    @PostMapping("/send-reset-code")
    public ApiResponse<Void> sendResetCode(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        if (username == null || username.isBlank() || email == null || email.isBlank()) {
            throw new BizException(4000, "账号和邮箱不能为空");
        }
        authService.sendResetCode(username, email);
        return ApiResponse.ok(null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        if (username == null || username.isBlank() || email == null || email.isBlank() || code == null || code.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new BizException(4000, "账号、邮箱、验证码和新密码不能为空");
        }
        authService.resetPassword(username, email, code, newPassword);
        return ApiResponse.ok(null);
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
