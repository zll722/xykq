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
import com.campus.attendance.mapper.AttendanceSessionMapper;
import com.campus.attendance.mapper.NotifyMessageMapper;
import com.campus.attendance.mapper.ScheduleMapper;
import com.campus.attendance.mapper.StudentProfileMapper;
import com.campus.attendance.service.AttendanceService;
import com.campus.attendance.service.SystemConfigService;
import com.campus.attendance.util.LocationUtils;
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
    private final SystemConfigService systemConfigService;
    private final AttendanceSessionMapper attendanceSessionMapper;

    public AttendanceServiceImpl(AttendanceRuleMapper attendanceRuleMapper,
                                 AttendanceRecordMapper attendanceRecordMapper,
                                 ScheduleMapper scheduleMapper,
                                 StudentProfileMapper studentProfileMapper,
                                 AttendanceAdjustmentMapper attendanceAdjustmentMapper,
                                 NotifyMessageMapper notifyMessageMapper,
                                 SystemConfigService systemConfigService,
                                 AttendanceSessionMapper attendanceSessionMapper) {
        this.attendanceRuleMapper = attendanceRuleMapper;
        this.attendanceRecordMapper = attendanceRecordMapper;
        this.scheduleMapper = scheduleMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.attendanceAdjustmentMapper = attendanceAdjustmentMapper;
        this.notifyMessageMapper = notifyMessageMapper;
        this.systemConfigService = systemConfigService;
        this.attendanceSessionMapper = attendanceSessionMapper;
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
        
        if (request.getLongitude() != null && request.getLatitude() != null) {
            if (schedule.getLatitude() == null || schedule.getLongitude() == null) {
                throw new BizException(4001, "该排班未配置考勤位置，无法进行位置签到");
            }
            double radius = schedule.getAttendanceRadius() != null ? schedule.getAttendanceRadius().doubleValue() : 200.0;
            double distance = LocationUtils.getDistance(
                    request.getLatitude(), request.getLongitude(),
                    schedule.getLatitude().doubleValue(), schedule.getLongitude().doubleValue());
            if (distance > radius) {
                throw new BizException(4001, "签到失败，超出考勤范围，当前距离中心点 " + Math.round(distance) + " 米");
            }
        }

        AttendanceRecord existing = attendanceRecordMapper.findOne(schedule.getId(), userId, attendanceDate);
        if (existing != null) {
            if ("SIGN_IN".equals(request.getSignType()) && existing.getSignInTime() != null) {
                throw new BizException(4009, "已完成课前签到，请勿重复签到");
            }
            if ("SIGN_OUT".equals(request.getSignType()) && existing.getSignOutTime() != null) {
                throw new BizException(4009, "已完成课后签退，请勿重复签退");
            }
        }

        AttendanceRule rule = attendanceRuleMapper.getActiveRule();
        if (rule == null) {
            rule = FALLBACK_RULE;
        }

        // Check if there is an active session
        com.campus.attendance.domain.AttendanceSession activeSession = attendanceSessionMapper.findActiveSession(schedule.getId(), attendanceDate, request.getSignType());
        
        if (activeSession == null) {
            // If no active manual or automatic session, enforce strict time windows
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime classStart = LocalDateTime.of(attendanceDate, schedule.getStartTime());
            LocalDateTime classEnd = LocalDateTime.of(attendanceDate, schedule.getEndTime());
            
            if ("SIGN_IN".equals(request.getSignType())) {
                LocalDateTime windowStart = classStart.plusMinutes(rule.getSignInStartOffsetMin());
                LocalDateTime windowEnd = classStart.plusMinutes(rule.getSignInEndOffsetMin());
                if (now.isBefore(windowStart) || now.isAfter(windowEnd)) {
                    throw new BizException(4001, "当前不在课前签到时间范围，且教师尚未手动发布签到");
                }
            } else if ("SIGN_OUT".equals(request.getSignType())) {
                LocalDateTime windowStart = classEnd.minusMinutes(5);
                LocalDateTime windowEnd = classEnd.plusMinutes(5);
                if (now.isBefore(windowStart) || now.isAfter(windowEnd)) {
                    throw new BizException(4001, "当前不在课后签退时间范围，且教师尚未手动发布签退");
                }
            } else {
                throw new BizException(4001, "未知的签到类型");
            }
        }

        LocalDateTime now = LocalDateTime.now();

        AttendanceRecord record = existing != null ? existing : new AttendanceRecord();
        if (existing == null) {
            record.setScheduleId(schedule.getId());
            record.setCourseId(schedule.getCourseId());
            record.setClassId(schedule.getClassId());
            record.setStudentId(userId);
            record.setAttendanceDate(attendanceDate);
            record.setStatus("ABSENT");
            record.setSource("AUTO");
        }

        if ("SIGN_IN".equals(request.getSignType())) {
            record.setSignInTime(now);
            record.setSignInLocation(request.getAddress());
        } else {
            record.setSignOutTime(now);
            record.setSignOutLocation(request.getAddress());
        }

        boolean hasSignIn = record.getSignInTime() != null;
        boolean hasSignOut = record.getSignOutTime() != null;
        
        if (hasSignIn && hasSignOut) {
            record.setStatus("PRESENT");
        } else if (!hasSignIn && hasSignOut) {
            record.setStatus("LATE");
        } else if (hasSignIn && !hasSignOut) {
            record.setStatus("EARLY_LEAVE");
        } else {
            record.setStatus("ABSENT");
        }

        if (existing == null) {
            attendanceRecordMapper.insert(record);
        } else {
            attendanceRecordMapper.updateSignResult(record.getId(), record.getStatus(), record.getSignInTime(), record.getSignOutTime(), record.getSignInLocation(), record.getSignOutLocation());
        }

        SignInResponse response = new SignInResponse();
        response.setStatus(record.getStatus());
        response.setSignedAt(now.toString());
        return response;
    }

    @Override
    public SignInResponse getMyScheduleRecord(Long userId, Long scheduleId) {
        AttendanceRecord record = attendanceRecordMapper.findOne(scheduleId, userId, LocalDate.now());
        if (record == null) {
            return null;
        }
        SignInResponse response = new SignInResponse();
        response.setStatus(record.getStatus());
        response.setSignedAt(record.getSignInTime() != null ? record.getSignInTime().toString() : (record.getSignOutTime() != null ? record.getSignOutTime().toString() : ""));
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
    public List<AdminAttendanceRecordItem> listTeacherExceptions(Long teacherId, Long courseId, Long classId, String attendanceDate) {
        LocalDate date = (attendanceDate == null || attendanceDate.isBlank()) ? null : LocalDate.parse(attendanceDate);
        if (date == null) {
            List<AdminAttendanceRecordItem> late = attendanceRecordMapper.listTeacherRecords(teacherId, courseId, classId, null, "LATE");
            List<AdminAttendanceRecordItem> absent = attendanceRecordMapper.listTeacherRecords(teacherId, courseId, classId, null, "ABSENT");
            List<AdminAttendanceRecordItem> early = attendanceRecordMapper.listTeacherRecords(teacherId, courseId, classId, null, "EARLY_LEAVE");
            late.addAll(absent);
            late.addAll(early);
            return late;
        }
        List<AdminAttendanceRecordItem> late = attendanceRecordMapper.listTeacherRecords(teacherId, courseId, classId, date, "LATE");
        List<AdminAttendanceRecordItem> absent = attendanceRecordMapper.listTeacherRecords(teacherId, courseId, classId, date, "ABSENT");
        List<AdminAttendanceRecordItem> early = attendanceRecordMapper.listTeacherRecords(teacherId, courseId, classId, date, "EARLY_LEAVE");
        late.addAll(absent);
        late.addAll(early);
        return late;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustRecordByTeacher(Long teacherId, Long recordId, AttendanceAdjustRequest request) {
        if (attendanceRecordMapper.checkTeacherCourseOwnership(teacherId, recordId) == 0) {
            throw new BizException(4003, "无权修改非本人授课的考勤记录");
        }
        adjustRecord(teacherId, recordId, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolveExceptionByTeacher(Long teacherId, Long recordId, AttendanceAdjustRequest request) {
        if (attendanceRecordMapper.checkTeacherCourseOwnership(teacherId, recordId) == 0) {
            throw new BizException(4003, "无权修改非本人授课的考勤记录");
        }
        resolveException(teacherId, recordId, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishSessionByTeacher(Long teacherId, Long scheduleId, String signType) {
        CourseSchedule schedule = scheduleMapper.findById(scheduleId);
        if (schedule == null) {
            throw new BizException(4001, "排班不存在");
        }
        // Verify ownership (simplified: should check if course belongs to teacher)
        // Here we do a quick check via attendanceRecordMapper or custom logic.
        // Actually, teacher has list of their own schedules in frontend.
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        com.campus.attendance.domain.AttendanceSession existing = attendanceSessionMapper.findActiveSession(scheduleId, today, signType);
        if (existing != null) {
            throw new BizException(4001, "该签到/签退已被发布，且正在进行中");
        }
        com.campus.attendance.domain.AttendanceSession session = new com.campus.attendance.domain.AttendanceSession();
        session.setScheduleId(schedule.getId());
        session.setCourseId(schedule.getCourseId());
        session.setClassId(schedule.getClassId());
        session.setAttendanceDate(today);
        session.setSignType(signType);
        session.setStatus("OPEN");
        session.setStartTime(now);
        session.setEndTime(now.plusMinutes(30)); // 30 mins window for manual
        attendanceSessionMapper.insert(session);
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
