package com.campus.attendance.mapper;

import com.campus.attendance.domain.SysPermission;
import com.campus.attendance.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<SysRole> listRoles();

    int insertRole(SysRole role);

    int updateRole(SysRole role);

    int deleteRole(@Param("id") Long id);

    List<SysPermission> listAllPermissions();

    List<Long> listPermissionIdsByRoleId(@Param("roleId") Long roleId);

    int deleteRolePermissions(@Param("roleId") Long roleId);

    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
