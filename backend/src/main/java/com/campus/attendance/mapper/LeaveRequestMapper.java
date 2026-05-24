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

    /**
     * 统计与指定时间段冲突的 PENDING 或 APPROVED 请假数量（用于防止重复提交）。
     *
     * @param studentId 学生 ID
     * @param startTime 新请假开始时间
     * @param endTime   新请假结束时间
     * @return 冲突记录数
     */
    int countConflict(@Param("studentId") Long studentId,
                      @Param("startTime") java.time.LocalDateTime startTime,
                      @Param("endTime") java.time.LocalDateTime endTime);
}
