package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.AttendanceRule;
import com.campus.attendance.dto.admin.AdminAttendanceRecordItem;
import com.campus.attendance.dto.attendance.AttendanceAdjustRequest;
import com.campus.attendance.dto.attendance.AttendanceRuleSaveRequest;
import com.campus.attendance.dto.attendance.SignInRequest;
import com.campus.attendance.dto.attendance.SignInResponse;
import com.campus.attendance.dto.user.MyAttendanceRecordItem;
import com.campus.attendance.dto.user.MyCalendarItem;
import com.campus.attendance.dto.user.MyOverviewStats;
import com.campus.attendance.dto.user.MyScheduleItem;
import com.campus.attendance.service.AttendanceService;
import com.campus.attendance.util.SecurityUtils;
import com.campus.attendance.security.RequirePermission;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/admin/attendance/rules")
    public ApiResponse<List<AttendanceRule>> listRules() {
        return ApiResponse.ok(attendanceService.listRules());
    }

    @PostMapping("/admin/attendance/rules")
    @RequirePermission("attendance:rule:update")
    public ApiResponse<Map<String, Long>> createRule(@Valid @RequestBody AttendanceRuleSaveRequest request) {
        return ApiResponse.ok(Map.of("id", attendanceService.createRule(request)));
    }

    @PutMapping("/admin/attendance/rules/{id}")
    @RequirePermission("attendance:rule:update")
    public ApiResponse<Void> updateRule(@PathVariable Long id, @Valid @RequestBody AttendanceRuleSaveRequest request) {
        attendanceService.updateRule(id, request);
        return ApiResponse.ok(null);
    }

    @PutMapping("/admin/attendance/rules/{id}/status")
    @RequirePermission("attendance:rule:update")
    public ApiResponse<Void> updateRuleStatus(@PathVariable Long id, @RequestParam Integer status) {
        attendanceService.updateRuleStatus(id, status);
        return ApiResponse.ok(null);
    }

    @GetMapping("/attendance/record/my/{scheduleId}")
    public ApiResponse<SignInResponse> getMyScheduleRecord(@PathVariable Long scheduleId) {
        return ApiResponse.ok(attendanceService.getMyScheduleRecord(SecurityUtils.getCurrentUserId(), scheduleId));
    }

    @PostMapping("/attendance/sign-in")
    public ApiResponse<SignInResponse> signIn(@Valid @RequestBody SignInRequest request) {
        return ApiResponse.ok(attendanceService.signIn(SecurityUtils.getCurrentUserId(), request));
    }

    @GetMapping("/attendance/records/my")
    public ApiResponse<List<MyAttendanceRecordItem>> listMyRecords() {
        return ApiResponse.ok(attendanceService.listMyRecords(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/schedules/my")
    public ApiResponse<List<MyScheduleItem>> listMySchedules() {
        return ApiResponse.ok(attendanceService.listMySchedules(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/admin/attendance/exceptions")
    public ApiResponse<List<AdminAttendanceRecordItem>> listExceptions(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String attendanceDate
    ) {
        return ApiResponse.ok(attendanceService.listExceptions(courseId, classId, attendanceDate));
    }

    @PostMapping("/admin/attendance/records/{id}/adjust")
    @RequirePermission("attendance:adjust")
    public ApiResponse<Void> adjust(@PathVariable Long id, @Valid @RequestBody AttendanceAdjustRequest request) {
        attendanceService.adjustRecord(SecurityUtils.getCurrentUserId(), id, request);
        return ApiResponse.ok(null);
    }

    @PutMapping("/admin/attendance/exceptions/{id}/resolve")
    @RequirePermission("attendance:resolve")
    public ApiResponse<Void> resolve(@PathVariable Long id, @Valid @RequestBody AttendanceAdjustRequest request) {
        attendanceService.resolveException(SecurityUtils.getCurrentUserId(), id, request);
        return ApiResponse.ok(null);
    }

    @GetMapping("/stats/my-overview")
    public ApiResponse<MyOverviewStats> myOverview() {
        return ApiResponse.ok(attendanceService.myOverview(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/stats/my-calendar")
    public ApiResponse<List<MyCalendarItem>> myCalendar(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        LocalDate start = (startDate == null || startDate.isBlank()) ? null : LocalDate.parse(startDate);
        LocalDate end = (endDate == null || endDate.isBlank()) ? null : LocalDate.parse(endDate);
        return ApiResponse.ok(attendanceService.myCalendar(SecurityUtils.getCurrentUserId(), start, end));
    }
}
