package com.campus.attendance.aspect;

import com.campus.attendance.domain.OperationLog;
import com.campus.attendance.service.OperationLogService;
import com.campus.attendance.util.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class OperationLogAspect {
    private final OperationLogService operationLogService;
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    public OperationLogAspect(OperationLogService operationLogService, HttpServletRequest request, ObjectMapper objectMapper) {
        this.operationLogService = operationLogService;
        this.request = request;
        this.objectMapper = objectMapper;
    }

    @AfterReturning(
            pointcut = "execution(* com.campus.attendance.controller..*.*(..)) && within(com.campus.attendance.controller..*)",
            returning = "retVal"
    )
    public void afterController(JoinPoint joinPoint, Object retVal) {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/v1/admin")) {
            return;
        }
        if (uri.startsWith("/api/v1/admin/system/logs")) {
            return;
        }
        OperationLog log = new OperationLog();
        try {
            log.setOperatorId(SecurityUtils.getCurrentUserId());
        } catch (Exception e) {
            log.setOperatorId(null);
        }
        log.setModuleName(extractModule(uri));
        log.setOperationType(request.getMethod());
        log.setRequestUri(uri);
        log.setRequestMethod(request.getMethod());
        log.setRequestParams(extractParams(joinPoint));
        log.setResultCode("0");
        log.setIpAddr(request.getRemoteAddr());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setOperatedAt(LocalDateTime.now());
        operationLogService.record(log);
    }

    private String extractModule(String uri) {
        String[] parts = uri.split("/");
        if (parts.length >= 5) {
            return parts[4];
        }
        return "admin";
    }

    private String extractParams(JoinPoint joinPoint) {
        try {
            String raw = Arrays.stream(joinPoint.getArgs())
                    .map(arg -> {
                        try {
                            return objectMapper.writeValueAsString(arg);
                        } catch (Exception e) {
                            return String.valueOf(arg);
                        }
                    })
                    .collect(Collectors.joining(","));
            if (raw.length() > 2000) {
                return raw.substring(0, 2000);
            }
            return raw;
        } catch (Exception e) {
            return "";
        }
    }
}
