package com.campus.attendance.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {
    List<String> listPermCodesByRoleCode(@Param("roleCode") String roleCode);
}
