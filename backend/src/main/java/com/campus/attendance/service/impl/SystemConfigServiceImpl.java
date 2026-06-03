package com.campus.attendance.service.impl;

import com.campus.attendance.domain.SystemConfig;
import com.campus.attendance.dto.system.SystemConfigUpdateItem;
import com.campus.attendance.mapper.SystemConfigMapper;
import com.campus.attendance.service.SystemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    private final SystemConfigMapper systemConfigMapper;

    public SystemConfigServiceImpl(SystemConfigMapper systemConfigMapper) {
        this.systemConfigMapper = systemConfigMapper;
    }

    @Override
    public List<SystemConfig> list() {
        return systemConfigMapper.listAll();
    }

    @Override
    public String getConfigValue(String key) {
        SystemConfig config = systemConfigMapper.findByKey(key);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(Long operatorId, List<SystemConfigUpdateItem> items) {
        for (SystemConfigUpdateItem item : items) {
            systemConfigMapper.updateValue(item.getConfigKey(), item.getConfigValue(), operatorId);
        }
    }
}
