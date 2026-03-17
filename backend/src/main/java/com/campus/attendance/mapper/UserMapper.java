package com.campus.attendance.mapper;

import com.campus.attendance.domain.SysUser;
import com.campus.attendance.dto.user.UserProfileInfo;
import com.campus.attendance.dto.user.UserProfileUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    SysUser findByUsername(@Param("username") String username);

    SysUser findById(@Param("id") Long id);

    int insertUser(SysUser user);

    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

    List<SysUser> listUsers(@Param("keyword") String keyword, @Param("status") Integer status, @Param("roleCode") String roleCode);

    UserProfileInfo getProfile(@Param("userId") Long userId);

    int updateProfile(@Param("userId") Long userId, @Param("profile") UserProfileUpdateRequest profile);

    int updateBasic(@Param("id") Long id,
                    @Param("realName") String realName,
                    @Param("roleCode") String roleCode,
                    @Param("status") Integer status);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int softDelete(@Param("id") Long id);
}
