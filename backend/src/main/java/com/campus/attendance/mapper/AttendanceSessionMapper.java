package com.campus.attendance.mapper;

import com.campus.attendance.domain.AttendanceSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceSessionMapper {
    void insert(AttendanceSession session);
    void update(AttendanceSession session);
    AttendanceSession findById(Long id);
    AttendanceSession findActiveSession(@Param("scheduleId") Long scheduleId, @Param("attendanceDate") LocalDate attendanceDate, @Param("signType") String signType);
    List<AttendanceSession> findSessionsByScheduleAndDate(@Param("scheduleId") Long scheduleId, @Param("attendanceDate") LocalDate attendanceDate);
}
