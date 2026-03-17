package com.campus.attendance.service.impl;

import com.campus.attendance.domain.SysPermission;
import com.campus.attendance.domain.SysRole;
import com.campus.attendance.dto.admin.RolePermissionUpdateRequest;
import com.campus.attendance.dto.admin.RoleSaveRequest;
import com.campus.attendance.mapper.RoleMapper;
import com.campus.attendance.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public List<SysRole> listRoles() {
        return roleMapper.listRoles();
    }

    @Override
    public Long createRole(RoleSaveRequest request) {
        SysRole role = new SysRole();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRemark(request.getRemark());
        roleMapper.insertRole(role);
        return role.getId();
    }

    @Override
    public void updateRole(Long id, RoleSaveRequest request) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRemark(request.getRemark());
        roleMapper.updateRole(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        roleMapper.deleteRolePermissions(id);
        roleMapper.deleteRole(id);
    }

    @Override
    public List<SysPermission> listAllPermissions() {
        return roleMapper.listAllPermissions();
    }

    @Override
    public List<Long> listPermissionIdsByRoleId(Long roleId) {
        return roleMapper.listPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRolePermissions(Long roleId, RolePermissionUpdateRequest request) {
        roleMapper.deleteRolePermissions(roleId);
        if (request.getPermissionIds() == null) {
            return;
        }
        for (Long permissionId : request.getPermissionIds()) {
            roleMapper.insertRolePermission(roleId, permissionId);
        }
    }
}
