package com.campus.attendance.service;

import com.campus.attendance.domain.SysUser;
import com.campus.attendance.dto.auth.ChangePasswordRequest;
import com.campus.attendance.dto.auth.LoginRequest;
import com.campus.attendance.dto.auth.LoginResponse;
import com.campus.attendance.dto.auth.RegisterRequest;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.PermissionMapper;
import com.campus.attendance.mapper.UserMapper;
import com.campus.attendance.security.JwtTokenProvider;
import com.campus.attendance.service.EmailService;
import com.campus.attendance.service.impl.AuthServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private PermissionMapper permissionMapper;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private EmailService emailService;
    @Mock
    private Cache<String, String> emailCodeCache;

    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.authService = new AuthServiceImpl(userMapper, permissionMapper, passwordEncoder, jwtTokenProvider, emailService, emailCodeCache);
    }

    @Test
    void loginSuccessShouldReturnTokenAndPermissions() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setRealName("管理员");
        user.setRoleCode("ADMIN");
        user.setStatus(1);
        user.setPasswordHash(passwordEncoder.encode("123456"));
        when(userMapper.findByUsername("admin")).thenReturn(user);
        when(jwtTokenProvider.generateToken(1L, "admin", "ADMIN")).thenReturn("jwt-token");
        when(permissionMapper.listPermCodesByRoleCode("ADMIN")).thenReturn(List.of("user:create", "role:update"));

        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("123456");
        LoginResponse resp = authService.login(req);

        Assertions.assertEquals("jwt-token", resp.getToken());
        Assertions.assertEquals(2, resp.getPermissions().size());
        Assertions.assertEquals("ADMIN", resp.getUserInfo().getRoleCode());
    }

    @Test
    void loginWrongPasswordShouldThrow() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setRoleCode("ADMIN");
        user.setStatus(1);
        user.setPasswordHash(passwordEncoder.encode("right-pass"));
        when(userMapper.findByUsername("admin")).thenReturn(user);

        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("wrong-pass");
        Assertions.assertThrows(BizException.class, () -> authService.login(req));
    }

    @Test
    void registerDuplicateUserShouldThrow() {
        SysUser existing = new SysUser();
        existing.setId(9L);
        when(userMapper.findByUsername("u1")).thenReturn(existing);
        RegisterRequest req = new RegisterRequest();
        req.setUsername("u1");
        req.setPassword("123456");
        req.setRealName("U");
        req.setStudentNo("s1");
        req.setClassId(1L);

        Assertions.assertThrows(BizException.class, () -> authService.register(req));
    }

    @Test
    void changePasswordSuccessShouldUpdateHash() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setPasswordHash(passwordEncoder.encode("old123"));
        when(userMapper.findById(1L)).thenReturn(user);
        when(userMapper.updatePassword(anyLong(), anyString())).thenReturn(1);

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("old123");
        req.setNewPassword("new123");
        authService.changePassword(1L, req);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userMapper).updatePassword(anyLong(), captor.capture());
        Assertions.assertTrue(passwordEncoder.matches("new123", captor.getValue()));
    }
}
