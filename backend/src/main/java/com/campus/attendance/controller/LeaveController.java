package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.LeaveRequest;
import com.campus.attendance.dto.leave.AdminLeaveApprovalHistoryItem;
import com.campus.attendance.dto.leave.AdminLeavePendingItem;
import com.campus.attendance.dto.leave.LeaveApplyRequest;
import com.campus.attendance.dto.leave.LeaveApproveRequest;
import com.campus.attendance.dto.leave.LeaveProgressItem;
import com.campus.attendance.security.RequirePermission;
import com.campus.attendance.service.LeaveService;
import com.campus.attendance.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class LeaveController {
    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/leave/requests")
    public ApiResponse<Map<String, Long>> apply(@Valid @RequestBody LeaveApplyRequest request) {
        Long id = leaveService.apply(SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.ok(Map.of("id", id));
    }

    @GetMapping("/leave/requests/my")
    public ApiResponse<List<LeaveRequest>> listMy() {
        return ApiResponse.ok(leaveService.listMy(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/leave/requests/my-progress")
    public ApiResponse<List<LeaveProgressItem>> listMyProgress() {
        return ApiResponse.ok(leaveService.listMyProgress(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/leave/requests/{id}")
    public ApiResponse<LeaveRequest> detail(@PathVariable Long id) {
        boolean adminMode = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(item -> "ROLE_ADMIN".equals(item.getAuthority()));
        return ApiResponse.ok(leaveService.detail(id, SecurityUtils.getCurrentUserId(), adminMode));
    }

    @PutMapping("/leave/requests/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        leaveService.cancel(id, SecurityUtils.getCurrentUserId());
        return ApiResponse.ok(null);
    }

    @GetMapping("/leave/approvals/pending")
    public ApiResponse<List<AdminLeavePendingItem>> listPending() {
        return ApiResponse.ok(leaveService.listPending());
    }

    @PostMapping("/leave/approvals/{requestId}")
    @RequirePermission("leave:approve")
    public ApiResponse<Void> approve(@PathVariable Long requestId, @Valid @RequestBody LeaveApproveRequest request) {
        leaveService.approve(requestId, SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.ok(null);
    }

    @GetMapping("/leave/approvals/history")
    public ApiResponse<List<AdminLeaveApprovalHistoryItem>> listHistory() {
        return ApiResponse.ok(leaveService.listApprovalHistory());
    }
}
