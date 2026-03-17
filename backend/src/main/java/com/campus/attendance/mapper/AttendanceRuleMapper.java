package com.campus.attendance.mapper;

import com.campus.attendance.domain.AttendanceRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceRuleMapper {
    List<AttendanceRule> listAll();

    AttendanceRule getActiveRule();

    int insert(AttendanceRule rule);

    int update(AttendanceRule rule);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
