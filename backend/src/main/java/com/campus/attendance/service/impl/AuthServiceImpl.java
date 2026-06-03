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
import com.campus.attendance.service.EmailService;
import com.github.benmanes.caffeine.cache.Cache;
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
    private final EmailService emailService;
    private final Cache<String, String> emailCodeCache;

    public AuthServiceImpl(UserMapper userMapper,
                           PermissionMapper permissionMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           EmailService emailService,
                           Cache<String, String> emailCodeCache) {
        this.userMapper = userMapper;
        this.permissionMapper = permissionMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.emailCodeCache = emailCodeCache;
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
    public void sendRegisterCode(String email) {
        SysUser existingEmail = userMapper.findByEmail(email);
        if (existingEmail != null) {
            throw new BizException(4001, "该邮箱已被注册");
        }
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        emailCodeCache.put(email, code);
        emailService.sendRegistrationCode(email, code);
    }

    @Override
    public void sendResetCode(String username, String email) {
        SysUser user = userMapper.findByUsername(username);
        if (user == null || user.getStatus() == 0) {
            throw new BizException(4003, "账号不存在或已禁用");
        }
        
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BizException(4003, "该账号未绑定邮箱，请联系管理员重置密码");
        }
        if (!user.getEmail().equals(email)) {
            throw new BizException(4003, "填写的邮箱与系统绑定的邮箱不匹配");
        }

        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        emailCodeCache.put(email, code);
        emailService.sendResetPasswordCode(email, code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String username, String email, String code, String newPassword) {
        String cachedCode = emailCodeCache.getIfPresent(email);
        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new BizException(4003, "验证码错误或已过期");
        }
        SysUser user = userMapper.findByUsername(username);
        if (user == null || user.getStatus() == 0) {
            throw new BizException(4003, "账号不存在或已禁用");
        }
        
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BizException(4003, "该账号未绑定邮箱，请联系管理员重置密码");
        }
        if (!user.getEmail().equals(email)) {
            throw new BizException(4003, "填写的邮箱与系统绑定的邮箱不匹配");
        }

        userMapper.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        emailCodeCache.invalidate(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        String cachedCode = emailCodeCache.getIfPresent(request.getEmail());
        if (cachedCode == null || !cachedCode.equals(request.getCode())) {
            throw new BizException(4003, "验证码错误或已过期");
        }

        SysUser existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            throw new BizException(4001, "用户名已存在");
        }
        SysUser existingEmail = userMapper.findByEmail(request.getEmail());
        if (existingEmail != null) {
            throw new BizException(4001, "邮箱已被注册");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setRoleCode("USER");
        user.setStatus(1);
        userMapper.insertUser(user);
        userMapper.insertStudentProfile(user.getId(), request.getStudentNo(), request.getClassId());
        emailCodeCache.invalidate(request.getEmail());
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
