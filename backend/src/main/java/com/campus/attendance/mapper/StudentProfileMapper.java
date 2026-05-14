package com.campus.attendance.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentProfileMapper {
    Long findClassIdByUserId(@Param("userId") Long userId);

    String findStudentNoByUserId(@Param("userId") Long userId);

    int upsertByUserId(@Param("userId") Long userId,
                       @Param("studentNo") String studentNo,
                       @Param("classId") Long classId);
}
