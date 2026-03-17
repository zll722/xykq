package com.campus.attendance.service;

import com.campus.attendance.domain.AttendanceRecord;
import com.campus.attendance.domain.AttendanceRule;
import com.campus.attendance.domain.CourseSchedule;
import com.campus.attendance.dto.attendance.AttendanceAdjustRequest;
import com.campus.attendance.dto.attendance.SignInRequest;
import com.campus.attendance.dto.attendance.SignInResponse;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.AttendanceAdjustmentMapper;
import com.campus.attendance.mapper.AttendanceRecordMapper;
import com.campus.attendance.mapper.AttendanceRuleMapper;
import com.campus.attendance.mapper.NotifyMessageMapper;
import com.campus.attendance.mapper.ScheduleMapper;
import com.campus.attendance.mapper.StudentProfileMapper;
import com.campus.attendance.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {
    @Mock
    private AttendanceRuleMapper attendanceRuleMapper;
    @Mock
    private AttendanceRecordMapper attendanceRecordMapper;
    @Mock
    private ScheduleMapper scheduleMapper;
    @Mock
    private StudentProfileMapper studentProfileMapper;
    @Mock
    private AttendanceAdjustmentMapper attendanceAdjustmentMapper;
    @Mock
    private NotifyMessageMapper notifyMessageMapper;

    private AttendanceServiceImpl attendanceService;

    @BeforeEach
    void setUp() {
        attendanceService = new AttendanceServiceImpl(
                attendanceRuleMapper,
                attendanceRecordMapper,
                scheduleMapper,
                studentProfileMapper,
                attendanceAdjustmentMapper,
                notifyMessageMapper
        );
    }

    @Test
    void signInPresentShouldInsertRecord() {
        long userId = 10L;
        CourseSchedule schedule = new CourseSchedule();
        schedule.setId(1L);
        schedule.setCourseId(2L);
        schedule.setClassId(3L);
        schedule.setStartTime(LocalTime.now().minusMinutes(5));
        when(scheduleMapper.findById(1L)).thenReturn(schedule);
        when(studentProfileMapper.findClassIdByUserId(userId)).thenReturn(3L);
        when(attendanceRecordMapper.findOne(anyLong(), anyLong(), any())).thenReturn(null);

        AttendanceRule rule = new AttendanceRule();
        rule.setSignInStartOffsetMin(-20);
        rule.setSignInEndOffsetMin(30);
        rule.setLateThresholdMin(10);
        rule.setAbsentThresholdMin(30);
        when(attendanceRuleMapper.getActiveRule()).thenReturn(rule);

        SignInRequest req = new SignInRequest();
        req.setScheduleId(1L);
        req.setAttendanceDate(LocalDate.now().toString());
        SignInResponse resp = attendanceService.signIn(userId, req);

        Assertions.assertEquals("PRESENT", resp.getStatus());
        verify(attendanceRecordMapper).insert(any(AttendanceRecord.class));
    }

    @Test
    void signInClassMismatchShouldThrow() {
        long userId = 10L;
        CourseSchedule schedule = new CourseSchedule();
        schedule.setId(1L);
        schedule.setClassId(3L);
        schedule.setStartTime(LocalTime.now());
        when(scheduleMapper.findById(1L)).thenReturn(schedule);
        when(studentProfileMapper.findClassIdByUserId(userId)).thenReturn(9L);

        SignInRequest req = new SignInRequest();
        req.setScheduleId(1L);
        Assertions.assertThrows(BizException.class, () -> attendanceService.signIn(userId, req));
    }

    @Test
    void adjustRecordShouldWriteAdjustmentAndNotify() {
        long operatorId = 1L;
        AttendanceRecord record = new AttendanceRecord();
        record.setId(100L);
        record.setStudentId(200L);
        record.setStatus("LATE");
        when(attendanceRecordMapper.findById(100L)).thenReturn(record);
        when(attendanceRecordMapper.updateStatusById(100L, "PRESENT")).thenReturn(1);

        AttendanceAdjustRequest req = new AttendanceAdjustRequest();
        req.setAfterStatus("PRESENT");
        req.setReason("补录证明已核验");

        attendanceService.adjustRecord(operatorId, 100L, req);

        verify(attendanceRecordMapper).updateStatusById(100L, "PRESENT");
        verify(attendanceAdjustmentMapper).insert(any());
        verify(notifyMessageMapper).insert(any());
    }
}
