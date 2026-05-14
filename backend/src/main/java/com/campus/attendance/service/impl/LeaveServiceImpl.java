package com.campus.attendance.service.impl;

import com.campus.attendance.domain.LeaveRequest;
import com.campus.attendance.domain.NotifyMessage;
import com.campus.attendance.dto.leave.AdminLeaveApprovalHistoryItem;
import com.campus.attendance.dto.leave.AdminLeavePendingItem;
import com.campus.attendance.dto.leave.LeaveApplyRequest;
import com.campus.attendance.dto.leave.LeaveApproveRequest;
import com.campus.attendance.dto.leave.LeaveProgressItem;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.LeaveApprovalMapper;
import com.campus.attendance.mapper.LeaveRequestMapper;
import com.campus.attendance.mapper.NotifyMessageMapper;
import com.campus.attendance.mapper.StudentProfileMapper;
import com.campus.attendance.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRequestMapper leaveRequestMapper;
    private final LeaveApprovalMapper leaveApprovalMapper;
    private final NotifyMessageMapper notifyMessageMapper;
    private final StudentProfileMapper studentProfileMapper;

    public LeaveServiceImpl(LeaveRequestMapper leaveRequestMapper,
                            LeaveApprovalMapper leaveApprovalMapper,
                            NotifyMessageMapper notifyMessageMapper,
                            StudentProfileMapper studentProfileMapper) {
        this.leaveRequestMapper = leaveRequestMapper;
        this.leaveApprovalMapper = leaveApprovalMapper;
        this.notifyMessageMapper = notifyMessageMapper;
        this.studentProfileMapper = studentProfileMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long apply(Long userId, LeaveApplyRequest request) {
        LocalDateTime start = LocalDateTime.parse(request.getStartTime());
        LocalDateTime end = LocalDateTime.parse(request.getEndTime());
        if (end.isBefore(start)) {
            throw new BizException(4001, "请假结束时间不能早于开始时间");
        }
        LeaveRequest leave = new LeaveRequest();
        leave.setStudentId(userId);
        leave.setLeaveType(request.getLeaveType());
        leave.setReason(request.getReason());
        leave.setStartTime(start);
        leave.setEndTime(end);
        leave.setProofUrl(request.getProofUrl());
        leave.setStatus("PENDING");
        leaveRequestMapper.insert(leave);
        return leave.getId();
    }

    @Override
    public List<LeaveRequest> listMy(Long userId) {
        return leaveRequestMapper.listMy(userId);
    }

    @Override
    public List<LeaveProgressItem> listMyProgress(Long userId) {
        return leaveRequestMapper.listMyProgress(userId);
    }

    @Override
    public LeaveRequest detail(Long id, Long userId, boolean adminMode) {
        LeaveRequest leave = leaveRequestMapper.findById(id);
        if (leave == null) {
            throw new BizException(4001, "请假记录不存在");
        }
        if (!adminMode && !leave.getStudentId().equals(userId)) {
            throw new BizException(4004, "无访问权限");
        }
        return leave;
    }

    @Override
    public void cancel(Long id, Long userId) {
        LeaveRequest leave = leaveRequestMapper.findById(id);
        if (leave == null) {
            throw new BizException(4001, "请假记录不存在");
        }
        if (!leave.getStudentId().equals(userId)) {
            throw new BizException(4004, "无访问权限");
        }
        if (!"PENDING".equals(leave.getStatus())) {
            throw new BizException(4001, "仅待审批状态可撤销");
        }
        leaveRequestMapper.updateStatus(id, "CANCELED");
    }

    @Override
    public List<AdminLeavePendingItem> listPending() {
        return leaveRequestMapper.listPendingReadable();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long requestId, Long approverId, LeaveApproveRequest request) {
        LeaveRequest leave = leaveRequestMapper.findById(requestId);
        if (leave == null) {
            throw new BizException(4001, "请假记录不存在");
        }
        if (!"PENDING".equals(leave.getStatus())) {
            throw new BizException(4001, "该请假记录已处理");
        }

        String action = request.getAction().toUpperCase();
        String status;
        if ("APPROVED".equals(action)) {
            status = "APPROVED";
        } else if ("REJECTED".equals(action)) {
            status = "REJECTED";
        } else {
            throw new BizException(4001, "审批动作仅支持 APPROVED 或 REJECTED");
        }
        leaveRequestMapper.updateStatus(requestId, status);

        com.campus.attendance.domain.LeaveApproval approval = new com.campus.attendance.domain.LeaveApproval();
        approval.setLeaveRequestId(requestId);
        approval.setApproverId(approverId);
        approval.setAction(action);
        approval.setComment(request.getComment());
        leaveApprovalMapper.insert(approval);

        NotifyMessage msg = new NotifyMessage();
        msg.setUserId(leave.getStudentId());
        msg.setTitle("请假审批结果");
        msg.setContent("您的请假申请#" + requestId + " 已被处理，结果为: " + status);
        msg.setMsgType("LEAVE_RESULT");
        msg.setReadFlag(0);
        msg.setSentAt(LocalDateTime.now());
        notifyMessageMapper.insert(msg);

        if ("APPROVED".equals(status)) {
            notifyTeachersOnApproval(requestId, leave);
        }
    }

    private void notifyTeachersOnApproval(Long requestId, LeaveRequest leave) {
        Long classId = studentProfileMapper.findClassIdByUserId(leave.getStudentId());
        if (classId == null) {
            return;
        }
        LocalDate startDate = leave.getStartTime().toLocalDate();
        LocalDate endDate = leave.getEndTime().toLocalDate();
        java.util.Set<Long> notifiedTeachers = new java.util.HashSet<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Long> teacherIds = leaveRequestMapper.findTeacherIdsByClassAndTimeRange(classId, date);
            for (Long teacherId : teacherIds) {
                if (notifiedTeachers.add(teacherId)) {
                    NotifyMessage teacherMsg = new NotifyMessage();
                    teacherMsg.setUserId(teacherId);
                    teacherMsg.setTitle("学生请假通知");
                    teacherMsg.setContent("学生请假申请#" + requestId + " 已审批通过，请假时段：" +
                            leave.getStartTime().toLocalDate() + " 至 " + leave.getEndTime().toLocalDate() +
                            "，请注意课堂出勤安排。");
                    teacherMsg.setMsgType("LEAVE_TEACHER_NOTIFY");
                    teacherMsg.setReadFlag(0);
                    teacherMsg.setSentAt(LocalDateTime.now());
                    notifyMessageMapper.insert(teacherMsg);
                }
            }
        }
    }

    @Override
    public List<AdminLeaveApprovalHistoryItem> listApprovalHistory() {
        return leaveApprovalMapper.listReadableHistory();
    }
}
