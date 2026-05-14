package com.campus.attendance.service;

import com.campus.attendance.domain.ClassInfo;
import com.campus.attendance.dto.admin.AdminUserSaveRequest;
import com.campus.attendance.dto.user.UserSummary;
import com.campus.attendance.dto.user.UserProfileInfo;
import com.campus.attendance.dto.user.UserProfileUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserSummary> listUsers(String keyword, Integer status, String roleCode);

    UserProfileInfo getProfile(Long userId);

    void updateProfile(Long userId, UserProfileUpdateRequest request);

    Long createAdminUser(AdminUserSaveRequest request);

    void updateAdminUser(Long id, AdminUserSaveRequest request);

    void updateUserStatus(Long id, Integer status);

    void deleteUser(Long id);

    List<ClassInfo> listAssignableClasses();
}
