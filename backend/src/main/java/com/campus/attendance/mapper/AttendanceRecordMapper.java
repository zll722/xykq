package com.campus.attendance.mapper;

import com.campus.attendance.domain.AttendanceRecord;
import com.campus.attendance.dto.admin.AdminAttendanceRecordItem;
import com.campus.attendance.dto.user.MyCalendarItem;
import com.campus.attendance.dto.user.MyAttendanceRecordItem;
import com.campus.attendance.dto.user.MyOverviewStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AttendanceRecordMapper {
    AttendanceRecord findOne(@Param("scheduleId") Long scheduleId,
                             @Param("studentId") Long studentId,
                             @Param("attendanceDate") LocalDate attendanceDate);

    AttendanceRecord findById(@Param("id") Long id);

    int insert(AttendanceRecord record);

    int updateSignResult(@Param("id") Long id,
                         @Param("status") String status,
                         @Param("signInTime") LocalDateTime signInTime,
                         @Param("signOutTime") LocalDateTime signOutTime,
                         @Param("signInLocation") String signInLocation,
                         @Param("signOutLocation") String signOutLocation);

    List<MyAttendanceRecordItem> listMyRecords(@Param("studentId") Long studentId);

    MyOverviewStats myOverview(@Param("studentId") Long studentId);

    List<MyCalendarItem> myCalendar(@Param("studentId") Long studentId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    List<AdminAttendanceRecordItem> listAdminRecords(@Param("courseId") Long courseId,
                                                     @Param("classId") Long classId,
                                                     @Param("attendanceDate") LocalDate attendanceDate,
                                                     @Param("status") String status);

    List<AdminAttendanceRecordItem> listAdminRecordsByRange(@Param("courseId") Long courseId,
                                                            @Param("classId") Long classId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    List<AdminAttendanceRecordItem> listTeacherRecords(@Param("teacherId") Long teacherId,
                                                       @Param("courseId") Long courseId,
                                                       @Param("classId") Long classId,
                                                       @Param("attendanceDate") LocalDate attendanceDate,
                                                       @Param("status") String status);

    int checkTeacherCourseOwnership(@Param("teacherId") Long teacherId, @Param("recordId") Long recordId);

    int updateStatusById(@Param("id") Long id, @Param("status") String status);
}
