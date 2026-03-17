package com.campus.attendance.service;

import com.campus.attendance.domain.LeaveRequest;
import com.campus.attendance.dto.leave.LeaveApplyRequest;
import com.campus.attendance.dto.leave.LeaveApproveRequest;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.mapper.LeaveApprovalMapper;
import com.campus.attendance.mapper.LeaveRequestMapper;
import com.campus.attendance.mapper.NotifyMessageMapper;
import com.campus.attendance.service.impl.LeaveServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {
    @Mock
    private LeaveRequestMapper leaveRequestMapper;
    @Mock
    private LeaveApprovalMapper leaveApprovalMapper;
    @Mock
    private NotifyMessageMapper notifyMessageMapper;

    private LeaveServiceImpl leaveService;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRequestMapper, leaveApprovalMapper, notifyMessageMapper);
    }

    @Test
    void applyWithInvalidTimeRangeShouldThrow() {
        LeaveApplyRequest req = new LeaveApplyRequest();
        req.setLeaveType("病假");
        req.setReason("test");
        req.setStartTime("2026-03-20T10:00:00");
        req.setEndTime("2026-03-19T10:00:00");
        Assertions.assertThrows(BizException.class, () -> leaveService.apply(1L, req));
    }

    @Test
    void approveShouldUpdateStatusAndWriteApprovalAndNotify() {
        LeaveRequest leave = new LeaveRequest();
        leave.setId(11L);
        leave.setStudentId(100L);
        leave.setStatus("PENDING");
        when(leaveRequestMapper.findById(11L)).thenReturn(leave);
        when(leaveRequestMapper.updateStatus(11L, "APPROVED")).thenReturn(1);
        when(leaveApprovalMapper.insert(any())).thenReturn(1);
        when(notifyMessageMapper.insert(any())).thenReturn(1);

        LeaveApproveRequest req = new LeaveApproveRequest();
        req.setAction("APPROVED");
        req.setComment("ok");
        leaveService.approve(11L, 2L, req);

        verify(leaveRequestMapper).updateStatus(11L, "APPROVED");
        verify(leaveApprovalMapper).insert(any());
        verify(notifyMessageMapper).insert(any());
    }
}
