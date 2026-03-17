package com.campus.attendance.service.impl;

import com.campus.attendance.domain.ClassInfo;
import com.campus.attendance.domain.CourseInfo;
import com.campus.attendance.domain.CourseSchedule;
import com.campus.attendance.dto.admin.ClassSaveRequest;
import com.campus.attendance.dto.admin.CourseSaveRequest;
import com.campus.attendance.dto.admin.ScheduleSaveRequest;
import com.campus.attendance.mapper.ClassMapper;
import com.campus.attendance.mapper.CourseMapper;
import com.campus.attendance.mapper.ScheduleMapper;
import com.campus.attendance.service.AdminTeachingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class AdminTeachingServiceImpl implements AdminTeachingService {
    private final ClassMapper classMapper;
    private final CourseMapper courseMapper;
    private final ScheduleMapper scheduleMapper;

    public AdminTeachingServiceImpl(ClassMapper classMapper, CourseMapper courseMapper, ScheduleMapper scheduleMapper) {
        this.classMapper = classMapper;
        this.courseMapper = courseMapper;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public List<ClassInfo> listClasses(String keyword, Integer status) {
        return classMapper.list(keyword, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createClass(ClassSaveRequest request) {
        ClassInfo info = new ClassInfo();
        info.setClassCode(request.getClassCode());
        info.setClassName(request.getClassName());
        info.setHeadTeacherId(request.getHeadTeacherId());
        info.setCapacity(request.getCapacity());
        info.setStatus(request.getStatus());
        info.setRemark(request.getRemark());
        classMapper.insert(info);
        return info.getId();
    }

    @Override
    public void updateClass(Long id, ClassSaveRequest request) {
        ClassInfo info = new ClassInfo();
        info.setId(id);
        info.setClassName(request.getClassName());
        info.setHeadTeacherId(request.getHeadTeacherId());
        info.setCapacity(request.getCapacity());
        info.setStatus(request.getStatus());
        info.setRemark(request.getRemark());
        classMapper.update(info);
    }

    @Override
    public void deleteClass(Long id) {
        classMapper.deleteById(id);
    }

    @Override
    public List<CourseInfo> listCourses(String keyword, Integer status) {
        return courseMapper.list(keyword, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCourse(CourseSaveRequest request) {
        CourseInfo info = new CourseInfo();
        info.setCourseCode(request.getCourseCode());
        info.setCourseName(request.getCourseName());
        info.setTeacherId(request.getTeacherId());
        info.setLocation(request.getLocation());
        info.setWeekDay(request.getWeekDay());
        info.setStartTime(LocalTime.parse(request.getStartTime()));
        info.setEndTime(LocalTime.parse(request.getEndTime()));
        info.setStatus(request.getStatus());
        courseMapper.insert(info);
        return info.getId();
    }

    @Override
    public void updateCourse(Long id, CourseSaveRequest request) {
        CourseInfo info = new CourseInfo();
        info.setId(id);
        info.setCourseName(request.getCourseName());
        info.setTeacherId(request.getTeacherId());
        info.setLocation(request.getLocation());
        info.setWeekDay(request.getWeekDay());
        info.setStartTime(LocalTime.parse(request.getStartTime()));
        info.setEndTime(LocalTime.parse(request.getEndTime()));
        info.setStatus(request.getStatus());
        courseMapper.update(info);
    }

    @Override
    public void deleteCourse(Long id) {
        courseMapper.deleteById(id);
    }

    @Override
    public List<CourseSchedule> listSchedules(Long courseId, Long classId) {
        return scheduleMapper.list(courseId, classId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSchedule(ScheduleSaveRequest request) {
        CourseSchedule schedule = new CourseSchedule();
        schedule.setCourseId(request.getCourseId());
        schedule.setClassId(request.getClassId());
        schedule.setWeekNo(request.getWeekNo());
        schedule.setWeekDay(request.getWeekDay());
        schedule.setStartTime(LocalTime.parse(request.getStartTime()));
        schedule.setEndTime(LocalTime.parse(request.getEndTime()));
        schedule.setLocation(request.getLocation());
        scheduleMapper.insert(schedule);
        return schedule.getId();
    }

    @Override
    public void updateSchedule(Long id, ScheduleSaveRequest request) {
        CourseSchedule schedule = new CourseSchedule();
        schedule.setId(id);
        schedule.setCourseId(request.getCourseId());
        schedule.setClassId(request.getClassId());
        schedule.setWeekNo(request.getWeekNo());
        schedule.setWeekDay(request.getWeekDay());
        schedule.setStartTime(LocalTime.parse(request.getStartTime()));
        schedule.setEndTime(LocalTime.parse(request.getEndTime()));
        schedule.setLocation(request.getLocation());
        scheduleMapper.update(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleMapper.deleteById(id);
    }
}
