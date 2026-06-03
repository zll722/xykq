package com.campus.attendance.mapper;

import com.campus.attendance.domain.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemConfigMapper {
    List<SystemConfig> listAll();
    
    SystemConfig findByKey(@Param("configKey") String configKey);

    int updateValue(@Param("configKey") String configKey,
                    @Param("configValue") String configValue,
                    @Param("updatedBy") Long updatedBy);
}
