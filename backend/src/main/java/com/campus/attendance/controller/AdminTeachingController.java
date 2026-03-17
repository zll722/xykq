package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.ClassInfo;
import com.campus.attendance.domain.CourseInfo;
import com.campus.attendance.domain.CourseSchedule;
import com.campus.attendance.dto.admin.ClassSaveRequest;
import com.campus.attendance.dto.admin.CourseSaveRequest;
import com.campus.attendance.dto.admin.ScheduleSaveRequest;
import com.campus.attendance.security.RequirePermission;
import com.campus.attendance.service.AdminTeachingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminTeachingController {
    private final AdminTeachingService adminTeachingService;

    public AdminTeachingController(AdminTeachingService adminTeachingService) {
        this.adminTeachingService = adminTeachingService;
    }

    @GetMapping("/classes")
    public ApiResponse<List<ClassInfo>> listClasses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.ok(adminTeachingService.listClasses(keyword, status));
    }

    @PostMapping("/classes")
    @RequirePermission("class:create")
    public ApiResponse<Map<String, Long>> createClass(@Valid @RequestBody ClassSaveRequest request) {
        return ApiResponse.ok(Map.of("id", adminTeachingService.createClass(request)));
    }

    @PutMapping("/classes/{id}")
    @RequirePermission("class:update")
    public ApiResponse<Void> updateClass(@PathVariable Long id, @Valid @RequestBody ClassSaveRequest request) {
        adminTeachingService.updateClass(id, request);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/classes/{id}")
    @RequirePermission("class:delete")
    public ApiResponse<Void> deleteClass(@PathVariable Long id) {
        adminTeachingService.deleteClass(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/courses")
    public ApiResponse<List<CourseInfo>> listCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.ok(adminTeachingService.listCourses(keyword, status));
    }

    @PostMapping("/courses")
    @RequirePermission("course:create")
    public ApiResponse<Map<String, Long>> createCourse(@Valid @RequestBody CourseSaveRequest request) {
        return ApiResponse.ok(Map.of("id", adminTeachingService.createCourse(request)));
    }

    @PutMapping("/courses/{id}")
    @RequirePermission("course:update")
    public ApiResponse<Void> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseSaveRequest request) {
        adminTeachingService.updateCourse(id, request);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/courses/{id}")
    @RequirePermission("course:delete")
    public ApiResponse<Void> deleteCourse(@PathVariable Long id) {
        adminTeachingService.deleteCourse(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/schedules")
    public ApiResponse<List<CourseSchedule>> listSchedules(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId
    ) {
        return ApiResponse.ok(adminTeachingService.listSchedules(courseId, classId));
    }

    @PostMapping("/schedules")
    @RequirePermission("schedule:create")
    public ApiResponse<Map<String, Long>> createSchedule(@Valid @RequestBody ScheduleSaveRequest request) {
        return ApiResponse.ok(Map.of("id", adminTeachingService.createSchedule(request)));
    }

    @PutMapping("/schedules/{id}")
    @RequirePermission("schedule:update")
    public ApiResponse<Void> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleSaveRequest request) {
        adminTeachingService.updateSchedule(id, request);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/schedules/{id}")
    @RequirePermission("schedule:delete")
    public ApiResponse<Void> deleteSchedule(@PathVariable Long id) {
        adminTeachingService.deleteSchedule(id);
        return ApiResponse.ok(null);
    }
}
