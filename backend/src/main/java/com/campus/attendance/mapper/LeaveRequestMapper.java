package com.campus.attendance.mapper;

import com.campus.attendance.domain.LeaveRequest;
import com.campus.attendance.dto.leave.LeaveProgressItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LeaveRequestMapper {
    int insert(LeaveRequest request);

    LeaveRequest findById(@Param("id") Long id);

    List<LeaveRequest> listMy(@Param("studentId") Long studentId);

    List<LeaveProgressItem> listMyProgress(@Param("studentId") Long studentId);

    List<LeaveRequest> listPending();

    List<LeaveRequest> listAll();

    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
