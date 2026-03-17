package com.campus.attendance.service.impl;

import com.campus.attendance.domain.OperationLog;
import com.campus.attendance.mapper.OperationLogMapper;
import com.campus.attendance.service.OperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {
    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public void record(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }

    @Override
    public List<OperationLog> list(String moduleName) {
        return operationLogMapper.list(moduleName);
    }
}
