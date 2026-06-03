package com.campus.attendance.service;

import com.campus.attendance.domain.SystemConfig;
import com.campus.attendance.dto.system.SystemConfigUpdateItem;

import java.util.List;

public interface SystemConfigService {
    List<SystemConfig> list();

    String getConfigValue(String key);

    void updateBatch(Long operatorId, List<SystemConfigUpdateItem> items);
}
