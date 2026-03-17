package com.campus.attendance.dto.admin;

import java.util.List;

public class RolePermissionUpdateRequest {
    private List<Long> permissionIds;

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
