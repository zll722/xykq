package com.campus.attendance.service;

import com.campus.attendance.domain.OperationLog;

import java.util.List;

public interface OperationLogService {
    void record(OperationLog operationLog);

    List<OperationLog> list(String moduleName);
}
