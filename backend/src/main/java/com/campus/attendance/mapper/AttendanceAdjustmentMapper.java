package com.campus.attendance.mapper;

import com.campus.attendance.domain.AttendanceAdjustment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceAdjustmentMapper {
    int insert(AttendanceAdjustment adjustment);
}
