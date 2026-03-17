package com.campus.attendance.mapper;

import com.campus.attendance.domain.CourseSchedule;
import com.campus.attendance.dto.user.MyScheduleItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    List<CourseSchedule> list(@Param("courseId") Long courseId, @Param("classId") Long classId);

    CourseSchedule findById(@Param("id") Long id);

    List<MyScheduleItem> listMySchedules(@Param("userId") Long userId);

    int insert(CourseSchedule schedule);

    int update(CourseSchedule schedule);

    int deleteById(@Param("id") Long id);
}
