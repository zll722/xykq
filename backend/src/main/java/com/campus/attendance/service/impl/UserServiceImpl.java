package com.campus.attendance.service.impl;

import com.campus.attendance.domain.ClassInfo;
import com.campus.attendance.domain.SysUser;
import com.campus.attendance.dto.admin.AdminUserSaveRequest;
import com.campus.attendance.dto.user.UserProfileInfo;
import com.campus.attendance.dto.user.UserProfileUpdateRequest;
import com.campus.attendance.dto.user.UserSummary;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.ClassMapper;
import com.campus.attendance.mapper.StudentProfileMapper;
import com.campus.attendance.mapper.UserMapper;
import com.campus.attendance.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final ClassMapper classMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper,
                           ClassMapper classMapper,
                           StudentProfileMapper studentProfileMapper,
                           PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.classMapper = classMapper;
        this.studentProfileMapper = studentProfileMapper;
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
    @Transactional(rollbackFor = Exception.class)
    public Long createAdminUser(AdminUserSaveRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BizException(4001, "新增用户必须提供密码");
        }
        SysUser exists = userMapper.findByUsername(request.getUsername());
        if (exists != null) {
            throw new BizException(4001, "用户名已存在");
        }
        validateClassBinding(request);
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setRoleCode(request.getRoleCode());
        user.setStatus(request.getStatus());
        userMapper.insertUser(user);
        saveStudentProfile(user.getId(), request);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdminUser(Long id, AdminUserSaveRequest request) {
        validateClassBinding(request);
        userMapper.updateBasic(id, request.getRealName(), request.getEmail(), request.getRoleCode(), request.getStatus());
        saveStudentProfile(id, request);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.softDelete(id);
    }

    @Override
    public List<ClassInfo> listAssignableClasses() {
        return classMapper.list(null, 1);
    }

    private void validateClassBinding(AdminUserSaveRequest request) {
        if (!"USER".equals(request.getRoleCode())) {
            return;
        }
        if (request.getClassId() == null) {
            throw new BizException(4001, "普通用户必须选择班级");
        }
    }

    private void saveStudentProfile(Long userId, AdminUserSaveRequest request) {
        if (!"USER".equals(request.getRoleCode())) {
            return;
        }
        String studentNo = request.getStudentNo();
        if (studentNo == null || studentNo.isBlank()) {
            studentNo = studentProfileMapper.findStudentNoByUserId(userId);
            if (studentNo == null || studentNo.isBlank()) {
                studentNo = "AUTO" + userId;
            }
        }
        studentProfileMapper.upsertByUserId(userId, studentNo, request.getClassId());
    }

    private UserSummary toSummary(SysUser user) {
        UserSummary summary = new UserSummary();
        summary.setId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setRealName(user.getRealName());
        summary.setEmail(user.getEmail());
        summary.setRoleCode(user.getRoleCode());
        summary.setStatus(user.getStatus());
        if ("USER".equals(user.getRoleCode())) {
            summary.setClassId(studentProfileMapper.findClassIdByUserId(user.getId()));
            summary.setStudentNo(studentProfileMapper.findStudentNoByUserId(user.getId()));
        }
        return summary;
    }
}
