package com.campus.attendance.service;

import com.campus.attendance.domain.LeaveApproval;
import com.campus.attendance.domain.LeaveRequest;
import com.campus.attendance.dto.leave.LeaveApplyRequest;
import com.campus.attendance.dto.leave.LeaveApproveRequest;
import com.campus.attendance.dto.leave.LeaveProgressItem;

import java.util.List;

public interface LeaveService {
    Long apply(Long userId, LeaveApplyRequest request);

    List<LeaveRequest> listMy(Long userId);

    List<LeaveProgressItem> listMyProgress(Long userId);

    LeaveRequest detail(Long id, Long userId, boolean adminMode);

    void cancel(Long id, Long userId);

    List<LeaveRequest> listPending();

    void approve(Long requestId, Long approverId, LeaveApproveRequest request);

    List<LeaveApproval> listApprovalHistory();
}
