package com.campus.attendance.service;

import com.campus.attendance.domain.SysPermission;
import com.campus.attendance.domain.SysRole;
import com.campus.attendance.dto.admin.RolePermissionUpdateRequest;
import com.campus.attendance.dto.admin.RoleSaveRequest;

import java.util.List;

public interface RoleService {
    List<SysRole> listRoles();

    Long createRole(RoleSaveRequest request);

    void updateRole(Long id, RoleSaveRequest request);

    void deleteRole(Long id);

    List<SysPermission> listAllPermissions();

    List<Long> listPermissionIdsByRoleId(Long roleId);

    void updateRolePermissions(Long roleId, RolePermissionUpdateRequest request);
}
