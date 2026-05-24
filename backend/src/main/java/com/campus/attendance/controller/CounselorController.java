package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.dto.counselor.CounselorClassItem;
import com.campus.attendance.dto.counselor.CounselorClassStudentItem;
import com.campus.attendance.dto.counselor.CounselorLeaveHistoryItem;
import com.campus.attendance.dto.counselor.CounselorLeavePendingItem;
import com.campus.attendance.dto.leave.LeaveApproveRequest;
import com.campus.attendance.mapper.CounselorMapper;
import com.campus.attendance.service.LeaveService;
import com.campus.attendance.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/counselor")
public class CounselorController {

    private final CounselorMapper counselorMapper;
    private final LeaveService leaveService;

    public CounselorController(CounselorMapper counselorMapper, LeaveService leaveService) {
        this.counselorMapper = counselorMapper;
        this.leaveService = leaveService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Long counselorId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(counselorMapper.getDashboardStats(counselorId));
    }

    @GetMapping("/leave/pending")
    public ApiResponse<List<CounselorLeavePendingItem>> listPending() {
        Long counselorId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(counselorMapper.listPendingLeaves(counselorId));
    }

    @PostMapping("/leave/{requestId}/approve")
    public ApiResponse<Void> approve(@PathVariable Long requestId,
                                     @Valid @RequestBody LeaveApproveRequest request) {
        Long counselorId = SecurityUtils.getCurrentUserId();
        leaveService.approve(requestId, counselorId, request);
        return ApiResponse.ok(null);
    }

    @GetMapping("/leave/history")
    public ApiResponse<List<CounselorLeaveHistoryItem>> listHistory() {
        Long counselorId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(counselorMapper.listLeaveHistory(counselorId));
    }

    @GetMapping("/classes")
    public ApiResponse<List<CounselorClassItem>> listClasses() {
        Long counselorId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(counselorMapper.listMyClasses(counselorId));
    }

    @GetMapping("/classes/{classId}/students")
    public ApiResponse<List<CounselorClassStudentItem>> listClassStudents(@PathVariable Long classId) {
        Long counselorId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(counselorMapper.listClassStudents(classId, counselorId));
    }
}
