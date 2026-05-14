package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.ClassInfo;
import com.campus.attendance.dto.admin.AdminUserSaveRequest;
import com.campus.attendance.dto.user.UserSummary;
import com.campus.attendance.security.RequirePermission;
import com.campus.attendance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserSummary>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String roleCode
    ) {
        return ApiResponse.ok(userService.listUsers(keyword, status, roleCode));
    }

    @GetMapping("/classes")
    public ApiResponse<List<ClassInfo>> listAssignableClasses() {
        return ApiResponse.ok(userService.listAssignableClasses());
    }

    @PostMapping
    @RequirePermission("user:create")
    public ApiResponse<Map<String, Long>> create(@Valid @RequestBody AdminUserSaveRequest request) {
        return ApiResponse.ok(Map.of("id", userService.createAdminUser(request)));
    }

    @PutMapping("/{id}")
    @RequirePermission("user:update")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody AdminUserSaveRequest request) {
        userService.updateAdminUser(id, request);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}/status")
    @RequirePermission("user:status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("user:delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.ok(null);
    }
}
