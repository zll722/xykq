package com.campus.attendance.dto.attendance;

import jakarta.validation.constraints.NotNull;

public class SignInRequest {
    @NotNull
    private Long scheduleId;
    private String attendanceDate;

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
}
