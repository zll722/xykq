package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.SysPermission;
import com.campus.attendance.domain.SysRole;
import com.campus.attendance.dto.admin.RolePermissionUpdateRequest;
import com.campus.attendance.dto.admin.RoleSaveRequest;
import com.campus.attendance.security.RequirePermission;
import com.campus.attendance.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/roles")
public class AdminRoleController {
    private final RoleService roleService;

    public AdminRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<List<SysRole>> listRoles() {
        return ApiResponse.ok(roleService.listRoles());
    }

    @PostMapping
    @RequirePermission("role:update")
    public ApiResponse<Map<String, Long>> create(@Valid @RequestBody RoleSaveRequest request) {
        return ApiResponse.ok(Map.of("id", roleService.createRole(request)));
    }

    @PutMapping("/{id}")
    @RequirePermission("role:update")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody RoleSaveRequest request) {
        roleService.updateRole(id, request);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("role:update")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/permissions/all")
    public ApiResponse<List<SysPermission>> listAllPermissions() {
        return ApiResponse.ok(roleService.listAllPermissions());
    }

    @GetMapping("/{id}/permissions")
    public ApiResponse<List<Long>> listRolePermissions(@PathVariable Long id) {
        return ApiResponse.ok(roleService.listPermissionIdsByRoleId(id));
    }

    @PutMapping("/{id}/permissions")
    @RequirePermission("role:update")
    public ApiResponse<Void> updateRolePermissions(@PathVariable Long id, @RequestBody RolePermissionUpdateRequest request) {
        roleService.updateRolePermissions(id, request);
        return ApiResponse.ok(null);
    }
}
