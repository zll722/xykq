package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.dto.teacher.TeacherLeaveNotifyItem;
import com.campus.attendance.mapper.TeacherMapper;
import com.campus.attendance.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.attendance.dto.admin.AdminAttendanceRecordItem;
import com.campus.attendance.dto.attendance.AttendanceAdjustRequest;
import com.campus.attendance.service.AttendanceService;
import com.campus.attendance.mapper.AttendanceRecordMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;
import com.campus.attendance.dto.teacher.TeacherPublishRequest;

@RestController
@RequestMapping("/api/v1/teacher")
public class TeacherController {

    private final TeacherMapper teacherMapper;
    private final AttendanceService attendanceService;
    private final AttendanceRecordMapper attendanceRecordMapper;

    public TeacherController(TeacherMapper teacherMapper, AttendanceService attendanceService, AttendanceRecordMapper attendanceRecordMapper) {
        this.teacherMapper = teacherMapper;
        this.attendanceService = attendanceService;
        this.attendanceRecordMapper = attendanceRecordMapper;
    }

    @GetMapping("/leave-notifications")
    public ApiResponse<List<TeacherLeaveNotifyItem>> leaveNotifications() {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(teacherMapper.listLeaveNotifications(teacherId));
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(teacherMapper.getDashboardStats(teacherId));
    }

    @GetMapping("/classes")
    public ApiResponse<List<Map<String, Object>>> myClasses() {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(teacherMapper.listMyClasses(teacherId));
    }

    @GetMapping("/courses")
    public ApiResponse<List<Map<String, Object>>> myCourses() {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(teacherMapper.listMyCourses(teacherId));
    }

    @GetMapping("/schedules/today")
    public ApiResponse<List<Map<String, Object>>> todaySchedules() {
        Long teacherId = SecurityUtils.getCurrentUserId();
        int weekDay = LocalDate.now().getDayOfWeek().getValue();
        return ApiResponse.ok(teacherMapper.listTodaySchedules(teacherId, weekDay));
    }

    @GetMapping("/classes/{classId}/attendance")
    public ApiResponse<List<Map<String, Object>>> classAttendance(@PathVariable Long classId) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(teacherMapper.listClassAttendance(classId, teacherId));
    }

    @GetMapping("/attendance/records")
    public ApiResponse<List<AdminAttendanceRecordItem>> listTeacherRecords(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate attendanceDate,
            @RequestParam(required = false) String status
    ) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(attendanceRecordMapper.listTeacherRecords(teacherId, courseId, classId, attendanceDate, status));
    }

    @GetMapping("/attendance/exceptions")
    public ApiResponse<List<AdminAttendanceRecordItem>> listTeacherExceptions(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String attendanceDate
    ) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(attendanceService.listTeacherExceptions(teacherId, courseId, classId, attendanceDate));
    }

    @PostMapping("/attendance/records/{id}/adjust")
    public ApiResponse<Void> adjustRecord(@PathVariable Long id, @Valid @RequestBody AttendanceAdjustRequest request) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        attendanceService.adjustRecordByTeacher(teacherId, id, request);
        return ApiResponse.ok(null);
    }

    @PutMapping("/attendance/exceptions/{id}/resolve")
    public ApiResponse<Void> resolveException(@PathVariable Long id, @Valid @RequestBody AttendanceAdjustRequest request) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        attendanceService.resolveExceptionByTeacher(teacherId, id, request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/attendance/publish")
    public ApiResponse<Void> publishAttendance(@Valid @RequestBody TeacherPublishRequest request) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        attendanceService.publishSessionByTeacher(teacherId, request.getScheduleId(), request.getSignType());
        return ApiResponse.ok(null);
    }
}
