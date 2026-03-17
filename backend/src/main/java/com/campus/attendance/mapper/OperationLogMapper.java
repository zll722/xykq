package com.campus.attendance.mapper;

import com.campus.attendance.domain.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {
    int insert(OperationLog operationLog);

    List<OperationLog> list(@Param("moduleName") String moduleName);
}
