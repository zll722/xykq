package com.campus.attendance.service.impl;

import com.campus.attendance.domain.AttendanceRecord;
import com.campus.attendance.domain.AttendanceRule;
import com.campus.attendance.domain.CourseSchedule;
import com.campus.attendance.domain.NotifyMessage;
import com.campus.attendance.domain.AttendanceAdjustment;
import com.campus.attendance.dto.admin.AdminAttendanceRecordItem;
import com.campus.attendance.dto.attendance.AttendanceAdjustRequest;
import com.campus.attendance.dto.attendance.AttendanceRuleSaveRequest;
import com.campus.attendance.dto.attendance.SignInRequest;
import com.campus.attendance.dto.attendance.SignInResponse;
import com.campus.attendance.dto.user.MyAttendanceRecordItem;
import com.campus.attendance.dto.user.MyCalendarItem;
import com.campus.attendance.dto.user.MyOverviewStats;
import com.campus.attendance.dto.user.MyScheduleItem;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.AttendanceAdjustmentMapper;
import com.campus.attendance.mapper.AttendanceRecordMapper;
import com.campus.attendance.mapper.AttendanceRuleMapper;
import com.campus.attendance.mapper.NotifyMessageMapper;
import com.campus.attendance.mapper.ScheduleMapper;
import com.campus.attendance.mapper.StudentProfileMapper;
import com.campus.attendance.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private static final AttendanceRule FALLBACK_RULE;

    static {
        AttendanceRule rule = new AttendanceRule();
        rule.setSignInStartOffsetMin(-15);
        rule.setSignInEndOffsetMin(15);
        rule.setLateThresholdMin(10);
        rule.setAbsentThresholdMin(30);
        FALLBACK_RULE = rule;
    }

    private final AttendanceRuleMapper attendanceRuleMapper;
    private final AttendanceRecordMapper attendanceRecordMapper;
    private final ScheduleMapper scheduleMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final AttendanceAdjustmentMapper attendanceAdjustmentMapper;
    private final NotifyMessageMapper notifyMessageMapper;

    public AttendanceServiceImpl(AttendanceRuleMapper attendanceRuleMapper,
                                 AttendanceRecordMapper attendanceRecordMapper,
                                 ScheduleMapper scheduleMapper,
                                 StudentProfileMapper studentProfileMapper,
                                 AttendanceAdjustmentMapper attendanceAdjustmentMapper,
                                 NotifyMessageMapper notifyMessageMapper) {
        this.attendanceRuleMapper = attendanceRuleMapper;
        this.attendanceRecordMapper = attendanceRecordMapper;
        this.scheduleMapper = scheduleMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.attendanceAdjustmentMapper = attendanceAdjustmentMapper;
        this.notifyMessageMapper = notifyMessageMapper;
    }

    @Override
    public List<AttendanceRule> listRules() {
        return attendanceRuleMapper.listAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRule(AttendanceRuleSaveRequest request) {
        AttendanceRule rule = toRule(request);
        attendanceRuleMapper.insert(rule);
        return rule.getId();
    }

    @Override
    public void updateRule(Long id, AttendanceRuleSaveRequest request) {
        AttendanceRule rule = toRule(request);
        rule.setId(id);
        attendanceRuleMapper.update(rule);
    }

    @Override
    public void updateRuleStatus(Long id, Integer status) {
        attendanceRuleMapper.updateStatus(id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignInResponse signIn(Long userId, SignInRequest request) {
        LocalDate attendanceDate = request.getAttendanceDate() == null || request.getAttendanceDate().isBlank()
                ? LocalDate.now()
                : LocalDate.parse(request.getAttendanceDate());
        CourseSchedule schedule = scheduleMapper.findById(request.getScheduleId());
        if (schedule == null) {
            throw new BizException(4001, "排班不存在");
        }
        Long studentClassId = studentProfileMapper.findClassIdByUserId(userId);
        if (studentClassId == null || !studentClassId.equals(schedule.getClassId())) {
            throw new BizException(4004, "当前用户不在该排班班级");
        }

        AttendanceRecord existing = attendanceRecordMapper.findOne(schedule.getId(), userId, attendanceDate);
        if (existing != null && existing.getSignedAt() != null) {
            throw new BizException(4009, "请勿重复签到");
        }

        AttendanceRule rule = attendanceRuleMapper.getActiveRule();
        if (rule == null) {
            rule = FALLBACK_RULE;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStart = LocalDateTime.of(attendanceDate, schedule.getStartTime());
        LocalDateTime windowStart = classStart.plusMinutes(rule.getSignInStartOffsetMin());
        LocalDateTime windowEnd = classStart.plusMinutes(rule.getSignInEndOffsetMin());
        if (now.isBefore(windowStart) || now.isAfter(windowEnd)) {
            throw new BizException(4001, "当前不在签到时间范围");
        }

        long minutesAfterClassStart = Duration.between(classStart, now).toMinutes();
        String signStatus;
        if (minutesAfterClassStart <= rule.getLateThresholdMin()) {
            signStatus = "PRESENT";
        } else if (minutesAfterClassStart <= rule.getAbsentThresholdMin()) {
            signStatus = "LATE";
        } else {
            signStatus = "ABSENT";
        }

        if (existing == null) {
            AttendanceRecord record = new AttendanceRecord();
            record.setScheduleId(schedule.getId());
            record.setCourseId(schedule.getCourseId());
            record.setClassId(schedule.getClassId());
            record.setStudentId(userId);
            record.setAttendanceDate(attendanceDate);
            record.setSignedAt(now);
            record.setStatus(signStatus);
            record.setSource("AUTO");
            attendanceRecordMapper.insert(record);
        } else {
            attendanceRecordMapper.updateSignResult(existing.getId(), signStatus, now);
        }

        SignInResponse response = new SignInResponse();
        response.setStatus(signStatus);
        response.setSignedAt(now.toString());
        return response;
    }

    @Override
    public SignInResponse getMyScheduleRecord(Long userId, Long scheduleId) {
        AttendanceRecord record = attendanceRecordMapper.findOne(scheduleId, userId, LocalDate.now());
        if (record == null || record.getSignedAt() == null) {
            return null;
        }
        SignInResponse response = new SignInResponse();
        response.setStatus(record.getStatus());
        response.setSignedAt(record.getSignedAt().toString());
        return response;
    }

    @Override
    public List<MyAttendanceRecordItem> listMyRecords(Long userId) {
        return attendanceRecordMapper.listMyRecords(userId);
    }

    @Override
    public List<MyScheduleItem> listMySchedules(Long userId) {
        return scheduleMapper.listMySchedules(userId);
    }

    @Override
    public List<AdminAttendanceRecordItem> listExceptions(Long courseId, Long classId, String attendanceDate) {
        LocalDate date = (attendanceDate == null || attendanceDate.isBlank()) ? null : LocalDate.parse(attendanceDate);
        if (date == null) {
            List<AdminAttendanceRecordItem> late = attendanceRecordMapper.listAdminRecords(courseId, classId, null, "LATE");
            List<AdminAttendanceRecordItem> absent = attendanceRecordMapper.listAdminRecords(courseId, classId, null, "ABSENT");
            List<AdminAttendanceRecordItem> early = attendanceRecordMapper.listAdminRecords(courseId, classId, null, "EARLY_LEAVE");
            late.addAll(absent);
            late.addAll(early);
            return late;
        }
        List<AdminAttendanceRecordItem> late = attendanceRecordMapper.listAdminRecords(courseId, classId, date, "LATE");
        List<AdminAttendanceRecordItem> absent = attendanceRecordMapper.listAdminRecords(courseId, classId, date, "ABSENT");
        List<AdminAttendanceRecordItem> early = attendanceRecordMapper.listAdminRecords(courseId, classId, date, "EARLY_LEAVE");
        late.addAll(absent);
        late.addAll(early);
        return late;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustRecord(Long operatorId, Long recordId, AttendanceAdjustRequest request) {
        AttendanceRecord record = attendanceRecordMapper.findById(recordId);
        if (record == null) {
            throw new BizException(4001, "考勤记录不存在");
        }
        writeAdjustmentAndUpdate(operatorId, record, request.getAfterStatus(), request.getReason());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolveException(Long operatorId, Long recordId, AttendanceAdjustRequest request) {
        AttendanceRecord record = attendanceRecordMapper.findById(recordId);
        if (record == null) {
            throw new BizException(4001, "异常记录不存在");
        }
        writeAdjustmentAndUpdate(operatorId, record, request.getAfterStatus(), request.getReason());
    }

    @Override
    public MyOverviewStats myOverview(Long userId) {
        MyOverviewStats stats = attendanceRecordMapper.myOverview(userId);
        if (stats == null) {
            stats = new MyOverviewStats();
            stats.setTotalCount(0);
            stats.setPresentCount(0);
            stats.setLateCount(0);
            stats.setAbsentCount(0);
            stats.setPresentRate(0);
        }
        return stats;
    }

    @Override
    public List<MyCalendarItem> myCalendar(Long userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordMapper.myCalendar(userId, startDate, endDate);
    }

    private AttendanceRule toRule(AttendanceRuleSaveRequest request) {
        AttendanceRule rule = new AttendanceRule();
        rule.setRuleName(request.getRuleName());
        rule.setSignInStartOffsetMin(request.getSignInStartOffsetMin());
        rule.setSignInEndOffsetMin(request.getSignInEndOffsetMin());
        rule.setLateThresholdMin(request.getLateThresholdMin());
        rule.setAbsentThresholdMin(request.getAbsentThresholdMin());
        rule.setAllowMakeup(request.getAllowMakeup());
        rule.setStatus(request.getStatus());
        return rule;
    }

    private void writeAdjustmentAndUpdate(Long operatorId, AttendanceRecord record, String afterStatus, String reason) {
        attendanceRecordMapper.updateStatusById(record.getId(), afterStatus);
        AttendanceAdjustment adj = new AttendanceAdjustment();
        adj.setAttendanceRecordId(record.getId());
        adj.setBeforeStatus(record.getStatus());
        adj.setAfterStatus(afterStatus);
        adj.setReason(reason);
        adj.setOperatorId(operatorId);
        adj.setCreatedAt(LocalDateTime.now());
        attendanceAdjustmentMapper.insert(adj);

        NotifyMessage msg = new NotifyMessage();
        msg.setUserId(record.getStudentId());
        msg.setTitle("考勤状态已调整");
        msg.setContent("您的考勤记录#" + record.getId() + " 已由管理员调整为 " + afterStatus + "，原因：" + reason);
        msg.setMsgType("ATT_EXCEPTION");
        msg.setReadFlag(0);
        msg.setSentAt(LocalDateTime.now());
        notifyMessageMapper.insert(msg);
    }
}
