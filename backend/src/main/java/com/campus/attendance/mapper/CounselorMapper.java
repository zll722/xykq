package com.campus.attendance.mapper;

import com.campus.attendance.dto.counselor.CounselorClassItem;
import com.campus.attendance.dto.counselor.CounselorClassStudentItem;
import com.campus.attendance.dto.counselor.CounselorLeaveHistoryItem;
import com.campus.attendance.dto.counselor.CounselorLeavePendingItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CounselorMapper {

    List<CounselorLeavePendingItem> listPendingLeaves(@Param("counselorId") Long counselorId);

    List<CounselorLeaveHistoryItem> listLeaveHistory(@Param("counselorId") Long counselorId);

    List<CounselorClassItem> listMyClasses(@Param("counselorId") Long counselorId);

    List<CounselorClassStudentItem> listClassStudents(@Param("classId") Long classId,
                                                       @Param("counselorId") Long counselorId);

    Map<String, Object> getDashboardStats(@Param("counselorId") Long counselorId);
}
