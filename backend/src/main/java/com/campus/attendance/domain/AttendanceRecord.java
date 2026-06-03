package com.campus.attendance.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceRecord {
    private Long id;
    private Long scheduleId;
    private Long courseId;
    private Long classId;
    private Long studentId;
    private LocalDate attendanceDate;
    private LocalDateTime signInTime;
    private LocalDateTime signOutTime;
    private String signInLocation;
    private String signOutLocation;
    private String status;
    private String source;
    private String remark;
    private String makeupReason;
    private Long operatorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalDateTime getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(LocalDateTime signInTime) {
        this.signInTime = signInTime;
    }

    public LocalDateTime getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(LocalDateTime signOutTime) {
        this.signOutTime = signOutTime;
    }

    public String getSignInLocation() {
        return signInLocation;
    }

    public void setSignInLocation(String signInLocation) {
        this.signInLocation = signInLocation;
    }

    public String getSignOutLocation() {
        return signOutLocation;
    }

    public void setSignOutLocation(String signOutLocation) {
        this.signOutLocation = signOutLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMakeupReason() {
        return makeupReason;
    }

    public void setMakeupReason(String makeupReason) {
        this.makeupReason = makeupReason;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}
