package com.campus.attendance.domain;

public class AttendanceRule {
    private Long id;
    private String ruleName;
    private Integer signInStartOffsetMin;
    private Integer signInEndOffsetMin;
    private Integer lateThresholdMin;
    private Integer absentThresholdMin;
    private Integer allowMakeup;
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
