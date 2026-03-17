package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.dto.user.UserProfileInfo;
import com.campus.attendance.dto.user.UserProfileUpdateRequest;
import com.campus.attendance.dto.user.UserSummary;
import com.campus.attendance.service.UserService;
import com.campus.attendance.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserSummary>> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String roleCode
    ) {
        return ApiResponse.ok(userService.listUsers(keyword, status, roleCode));
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileInfo> me() {
        return ApiResponse.ok(userService.getProfile(SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/me")
    public ApiResponse<Void> updateMe(@RequestBody UserProfileUpdateRequest request) {
        userService.updateProfile(SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.ok(null);
    }
}
