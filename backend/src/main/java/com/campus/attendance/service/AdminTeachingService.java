package com.campus.attendance.service;

import com.campus.attendance.domain.ClassInfo;
import com.campus.attendance.domain.CourseInfo;
import com.campus.attendance.domain.CourseSchedule;
import com.campus.attendance.dto.admin.ClassSaveRequest;
import com.campus.attendance.dto.admin.CourseSaveRequest;
import com.campus.attendance.dto.admin.ScheduleSaveRequest;

import java.util.List;

public interface AdminTeachingService {
    List<ClassInfo> listClasses(String keyword, Integer status);

    Long createClass(ClassSaveRequest request);

    void updateClass(Long id, ClassSaveRequest request);

    void deleteClass(Long id);

    List<CourseInfo> listCourses(String keyword, Integer status);

    Long createCourse(CourseSaveRequest request);

    void updateCourse(Long id, CourseSaveRequest request);

    void deleteCourse(Long id);

    List<CourseSchedule> listSchedules(Long courseId, Long classId);

    Long createSchedule(ScheduleSaveRequest request);

    void updateSchedule(Long id, ScheduleSaveRequest request);

    void deleteSchedule(Long id);
}
