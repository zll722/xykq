package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.OperationLog;
import com.campus.attendance.domain.SystemConfig;
import com.campus.attendance.dto.system.SystemConfigUpdateItem;
import com.campus.attendance.security.RequirePermission;
import com.campus.attendance.service.OperationLogService;
import com.campus.attendance.service.SystemConfigService;
import com.campus.attendance.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/system")
public class AdminSystemController {
    private final OperationLogService operationLogService;
    private final SystemConfigService systemConfigService;

    public AdminSystemController(OperationLogService operationLogService, SystemConfigService systemConfigService) {
        this.operationLogService = operationLogService;
        this.systemConfigService = systemConfigService;
    }

    @GetMapping("/logs")
    public ApiResponse<List<OperationLog>> listLogs(@RequestParam(required = false) String moduleName) {
        return ApiResponse.ok(operationLogService.list(moduleName));
    }

    @GetMapping("/config")
    public ApiResponse<List<SystemConfig>> listConfig() {
        return ApiResponse.ok(systemConfigService.list());
    }

    @PutMapping("/config")
    @RequirePermission("system:config:update")
    public ApiResponse<Void> updateConfig(@Valid @RequestBody List<SystemConfigUpdateItem> items) {
        systemConfigService.updateBatch(SecurityUtils.getCurrentUserId(), items);
        return ApiResponse.ok(null);
    }
}
