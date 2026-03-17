package com.campus.attendance.aspect;

import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.PermissionMapper;
import com.campus.attendance.security.RequirePermission;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class PermissionGuardAspect {
    private final PermissionMapper permissionMapper;

    public PermissionGuardAspect(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BizException(4003, "未登录");
        }
        String roleCode = authentication.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .orElse("");
        List<String> perms = permissionMapper.listPermCodesByRoleCode(roleCode);
        boolean ok = Arrays.stream(requirePermission.value()).allMatch(perms::contains);
        if (!ok) {
            throw new BizException(4004, "无访问权限");
        }
    }
}
