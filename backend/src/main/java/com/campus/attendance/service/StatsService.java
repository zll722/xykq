package com.campus.attendance.service;

import com.campus.attendance.dto.admin.AdminAttendanceRecordItem;
import com.campus.attendance.dto.stats.AttendanceAbnormalStats;
import com.campus.attendance.dto.stats.AttendanceRateStats;

import java.time.LocalDate;
import java.util.List;

public interface StatsService {
    List<AdminAttendanceRecordItem> listAdminAttendanceRecords(Long courseId, Long classId, LocalDate attendanceDate, String status);

    AttendanceRateStats attendanceRate(Long courseId, Long classId, LocalDate startDate, LocalDate endDate);

    AttendanceAbnormalStats abnormalStats(Long courseId, Long classId, LocalDate startDate, LocalDate endDate);
}
