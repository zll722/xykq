package com.campus.attendance.dto.admin;

import jakarta.validation.constraints.NotBlank;

public class RoleSaveRequest {
    @NotBlank
    private String roleCode;
    @NotBlank
    private String roleName;
    private String remark;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
