package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.dto.teacher.TeacherLeaveNotifyItem;
import com.campus.attendance.mapper.TeacherMapper;
import com.campus.attendance.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teacher")
public class TeacherController {

    private final TeacherMapper teacherMapper;

    public TeacherController(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
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

    @GetMapping("/classes/{classId}/attendance")
    public ApiResponse<List<Map<String, Object>>> classAttendance(@PathVariable Long classId) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(teacherMapper.listClassAttendance(classId, teacherId));
    }
}
