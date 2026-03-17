package com.campus.attendance.mapper;

import com.campus.attendance.domain.LeaveApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LeaveApprovalMapper {
    int insert(LeaveApproval approval);

    List<LeaveApproval> listByRequestId(@Param("leaveRequestId") Long leaveRequestId);

    List<LeaveApproval> listAll();
}
