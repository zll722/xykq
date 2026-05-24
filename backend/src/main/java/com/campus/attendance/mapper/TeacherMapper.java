package com.campus.attendance.mapper;

import com.campus.attendance.dto.teacher.TeacherLeaveNotifyItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherMapper {

    List<TeacherLeaveNotifyItem> listLeaveNotifications(@Param("teacherId") Long teacherId);

    Map<String, Object> getDashboardStats(@Param("teacherId") Long teacherId);

    List<Map<String, Object>> listMyClasses(@Param("teacherId") Long teacherId);

    List<Map<String, Object>> listMyCourses(@Param("teacherId") Long teacherId);

    List<Map<String, Object>> listClassAttendance(@Param("classId") Long classId,
                                                   @Param("teacherId") Long teacherId);
}
