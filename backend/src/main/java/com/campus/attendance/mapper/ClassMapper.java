package com.campus.attendance.mapper;

import com.campus.attendance.domain.ClassInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassMapper {
    List<ClassInfo> list(@Param("keyword") String keyword, @Param("status") Integer status);

    int insert(ClassInfo classInfo);

    int update(ClassInfo classInfo);

    int deleteById(@Param("id") Long id);
}
