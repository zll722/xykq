package com.campus.attendance.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AttendanceRuleSaveRequest {
    @NotBlank
    private String ruleName;
    @NotNull
    private Integer signInStartOffsetMin;
    @NotNull
    private Integer signInEndOffsetMin;
    @NotNull
    private Integer lateThresholdMin;
    @NotNull
    private Integer absentThresholdMin;
    @NotNull
    private Integer allowMakeup;
    @NotNull
    private Integer status;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getSignInStartOffsetMin() {
        return signInStartOffsetMin;
    }

    public void setSignInStartOffsetMin(Integer signInStartOffsetMin) {
        this.signInStartOffsetMin = signInStartOffsetMin;
    }

    public Integer getSignInEndOffsetMin() {
        return signInEndOffsetMin;
    }

    public void setSignInEndOffsetMin(Integer signInEndOffsetMin) {
        this.signInEndOffsetMin = signInEndOffsetMin;
    }

    public Integer getLateThresholdMin() {
        return lateThresholdMin;
    }

    public void setLateThresholdMin(Integer lateThresholdMin) {
        this.lateThresholdMin = lateThresholdMin;
    }

    public Integer getAbsentThresholdMin() {
        return absentThresholdMin;
    }

    public void setAbsentThresholdMin(Integer absentThresholdMin) {
        this.absentThresholdMin = absentThresholdMin;
    }

    public Integer getAllowMakeup() {
        return allowMakeup;
    }

    public void setAllowMakeup(Integer allowMakeup) {
        this.allowMakeup = allowMakeup;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
