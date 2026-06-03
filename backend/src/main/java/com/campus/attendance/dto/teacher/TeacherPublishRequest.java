package com.campus.attendance.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TeacherPublishRequest {
    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;

    @NotBlank(message = "签到类型不能为空")
    private String signType; // SIGN_IN or SIGN_OUT

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }
}
