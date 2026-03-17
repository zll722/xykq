package com.campus.attendance.service.impl;

import com.campus.attendance.dto.admin.AdminAttendanceRecordItem;
import com.campus.attendance.dto.stats.AttendanceAbnormalStats;
import com.campus.attendance.dto.stats.AttendanceRateStats;
import com.campus.attendance.mapper.AttendanceRecordMapper;
import com.campus.attendance.service.StatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    private final AttendanceRecordMapper attendanceRecordMapper;

    public StatsServiceImpl(AttendanceRecordMapper attendanceRecordMapper) {
        this.attendanceRecordMapper = attendanceRecordMapper;
    }

    @Override
    public List<AdminAttendanceRecordItem> listAdminAttendanceRecords(Long courseId, Long classId, LocalDate attendanceDate, String status) {
        return attendanceRecordMapper.listAdminRecords(courseId, classId, attendanceDate, status);
    }

    @Override
    public AttendanceRateStats attendanceRate(Long courseId, Long classId, LocalDate startDate, LocalDate endDate) {
        List<AdminAttendanceRecordItem> records = attendanceRecordMapper.listAdminRecordsByRange(courseId, classId, startDate, endDate);
        long total = records.size();
        long presentCount = records.stream().filter(it -> "PRESENT".equals(it.getStatus())).count();
        long lateCount = records.stream().filter(it -> "LATE".equals(it.getStatus())).count();
        long absentCount = records.stream().filter(it -> "ABSENT".equals(it.getStatus())).count();

        AttendanceRateStats stats = new AttendanceRateStats();
        stats.setTotal(total);
        stats.setPresentCount(presentCount);
        stats.setLateCount(lateCount);
        stats.setAbsentCount(absentCount);
        if (total == 0) {
            stats.setPresentRate(0);
            stats.setLateRate(0);
            stats.setAbsentRate(0);
        } else {
            stats.setPresentRate(percent(presentCount, total));
            stats.setLateRate(percent(lateCount, total));
            stats.setAbsentRate(percent(absentCount, total));
        }
        return stats;
    }

    @Override
    public AttendanceAbnormalStats abnormalStats(Long courseId, Long classId, LocalDate startDate, LocalDate endDate) {
        List<AdminAttendanceRecordItem> records = attendanceRecordMapper.listAdminRecordsByRange(courseId, classId, startDate, endDate);
        AttendanceAbnormalStats stats = new AttendanceAbnormalStats();
        stats.setTotal(records.size());
        stats.setLateCount(records.stream().filter(it -> "LATE".equals(it.getStatus())).count());
        stats.setAbsentCount(records.stream().filter(it -> "ABSENT".equals(it.getStatus())).count());
        stats.setLeaveCount(records.stream().filter(it -> "LEAVE".equals(it.getStatus())).count());
        stats.setEarlyLeaveCount(records.stream().filter(it -> "EARLY_LEAVE".equals(it.getStatus())).count());
        return stats;
    }

    private double percent(long part, long total) {
        return Math.round((part * 10000.0 / total)) / 100.0;
    }
}
