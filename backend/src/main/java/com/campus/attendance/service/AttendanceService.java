package com.campus.attendance.service;

import com.campus.attendance.domain.AttendanceRule;
import com.campus.attendance.dto.admin.AdminAttendanceRecordItem;
import com.campus.attendance.dto.attendance.AttendanceAdjustRequest;
import com.campus.attendance.dto.attendance.AttendanceRuleSaveRequest;
import com.campus.attendance.dto.attendance.SignInRequest;
import com.campus.attendance.dto.attendance.SignInResponse;
import com.campus.attendance.dto.user.MyAttendanceRecordItem;
import com.campus.attendance.dto.user.MyCalendarItem;
import com.campus.attendance.dto.user.MyOverviewStats;
import com.campus.attendance.dto.user.MyScheduleItem;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<AttendanceRule> listRules();

    Long createRule(AttendanceRuleSaveRequest request);

    void updateRule(Long id, AttendanceRuleSaveRequest request);

    void updateRuleStatus(Long id, Integer status);

    SignInResponse signIn(Long userId, SignInRequest request);

    List<MyAttendanceRecordItem> listMyRecords(Long userId);

    List<MyScheduleItem> listMySchedules(Long userId);

    List<AdminAttendanceRecordItem> listExceptions(Long courseId, Long classId, String attendanceDate);

    void adjustRecord(Long operatorId, Long recordId, AttendanceAdjustRequest request);

    void resolveException(Long operatorId, Long recordId, AttendanceAdjustRequest request);

    MyOverviewStats myOverview(Long userId);

    List<MyCalendarItem> myCalendar(Long userId, LocalDate startDate, LocalDate endDate);
}
