package com.campus.attendance.service.impl;

import com.campus.attendance.domain.SysUser;
import com.campus.attendance.dto.admin.AdminUserSaveRequest;
import com.campus.attendance.dto.user.UserProfileInfo;
import com.campus.attendance.dto.user.UserProfileUpdateRequest;
import com.campus.attendance.dto.user.UserSummary;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.UserMapper;
import com.campus.attendance.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserSummary> listUsers(String keyword, Integer status, String roleCode) {
        List<SysUser> users = userMapper.listUsers(keyword, status, roleCode);
        return users.stream().map(this::toSummary).toList();
    }

    @Override
    public UserProfileInfo getProfile(Long userId) {
        UserProfileInfo profile = userMapper.getProfile(userId);
        if (profile == null) {
            throw new BizException(4001, "用户不存在");
        }
        return profile;
    }

    @Override
    public void updateProfile(Long userId, UserProfileUpdateRequest request) {
        userMapper.updateProfile(userId, request);
    }

    @Override
    public Long createAdminUser(AdminUserSaveRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BizException(4001, "新增用户必须提供密码");
        }
        SysUser exists = userMapper.findByUsername(request.getUsername());
        if (exists != null) {
            throw new BizException(4001, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setRoleCode(request.getRoleCode());
        user.setStatus(request.getStatus());
        userMapper.insertUser(user);
        return user.getId();
    }

    @Override
    public void updateAdminUser(Long id, AdminUserSaveRequest request) {
        userMapper.updateBasic(id, request.getRealName(), request.getRoleCode(), request.getStatus());
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.softDelete(id);
    }

    private UserSummary toSummary(SysUser user) {
        UserSummary summary = new UserSummary();
        summary.setId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setRealName(user.getRealName());
        summary.setRoleCode(user.getRoleCode());
        summary.setStatus(user.getStatus());
        return summary;
    }
}