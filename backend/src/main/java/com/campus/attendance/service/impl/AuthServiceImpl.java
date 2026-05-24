package com.campus.attendance.service.impl;

import com.campus.attendance.domain.SysUser;
import com.campus.attendance.dto.auth.ChangePasswordRequest;
import com.campus.attendance.dto.auth.LoginRequest;
import com.campus.attendance.dto.auth.LoginResponse;
import com.campus.attendance.dto.auth.RegisterRequest;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.PermissionMapper;
import com.campus.attendance.mapper.UserMapper;
import com.campus.attendance.security.JwtTokenProvider;
import com.campus.attendance.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserMapper userMapper,
                           PermissionMapper permissionMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.permissionMapper = permissionMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = userMapper.findByUsername(request.getUsername());
        if (user == null || user.getStatus() == 0) {
            throw new BizException(4003, "账号不存在或已禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BizException(4003, "账号或密码错误");
        }
        LoginResponse response = new LoginResponse();
        response.setToken(jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRoleCode()));
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRoleCode(user.getRoleCode());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        response.setUserInfo(userInfo);
        response.setPermissions(permissionMapper.listPermCodesByRoleCode(user.getRoleCode()));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        SysUser existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            throw new BizException(4001, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setRoleCode("USER");
        user.setStatus(1);
        userMapper.insertUser(user);
        userMapper.insertStudentProfile(user.getId(), request.getStudentNo(), request.getClassId());
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(4003, "用户不存在");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BizException(4001, "旧密码不正确");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(request.getNewPassword()));
    }

    @Override
    public LoginResponse.UserInfo me(Long userId) {
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(4003, "用户不存在");
        }
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRoleCode(user.getRoleCode());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        return userInfo;
    }

    @Override
    public List<String> myPermissions(Long userId) {
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(4003, "用户不存在");
        }
        return permissionMapper.listPermCodesByRoleCode(user.getRoleCode());
    }
}
