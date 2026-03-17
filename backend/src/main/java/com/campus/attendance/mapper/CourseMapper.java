package com.campus.attendance.mapper;

import com.campus.attendance.domain.CourseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {
    List<CourseInfo> list(@Param("keyword") String keyword, @Param("status") Integer status);

    int insert(CourseInfo courseInfo);

    int update(CourseInfo courseInfo);

    int deleteById(@Param("id") Long id);
}
