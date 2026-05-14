package com.campus.attendance.mapper;

import com.campus.attendance.domain.LeaveRequest;
import com.campus.attendance.dto.leave.AdminLeavePendingItem;
import com.campus.attendance.dto.leave.LeaveProgressItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LeaveRequestMapper {
    int insert(LeaveRequest leaveRequest);

    LeaveRequest findById(@Param("id") Long id);

    List<LeaveRequest> listMy(@Param("studentId") Long studentId);

    List<LeaveProgressItem> listMyProgress(@Param("studentId") Long studentId);

    List<AdminLeavePendingItem> listPendingReadable();

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    List<Long> findTeacherIdsByClassAndTimeRange(@Param("classId") Long classId,
                                                 @Param("leaveDate") LocalDate leaveDate);
}
