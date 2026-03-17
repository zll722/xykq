package com.campus.attendance.dto.attendance;

import jakarta.validation.constraints.NotBlank;

public class AttendanceAdjustRequest {
    @NotBlank
    private String afterStatus;
    @NotBlank
    private String reason;

    public String getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(String afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
