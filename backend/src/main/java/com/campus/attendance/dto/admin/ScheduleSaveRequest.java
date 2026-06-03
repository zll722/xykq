package com.campus.attendance.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScheduleSaveRequest {
    @NotNull
    private Long courseId;
    @NotNull
    private Long classId;
    private Integer weekNo;
    @NotNull
    @Min(1)
    @Max(7)
    private Integer weekDay;
    @NotBlank
    private String startTime;
    @NotBlank
    private String endTime;
    private String location;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private Integer attendanceRadius;
    private Boolean autoPublishAttendance;

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

    public Integer getWeekNo() {
        return weekNo;
    }

    public void setWeekNo(Integer weekNo) {
        this.weekNo = weekNo;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public java.math.BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(java.math.BigDecimal latitude) {
        this.latitude = latitude;
    }

    public java.math.BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(java.math.BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getAttendanceRadius() {
        return attendanceRadius;
    }

    public void setAttendanceRadius(Integer attendanceRadius) {
        this.attendanceRadius = attendanceRadius;
    }

    public Boolean getAutoPublishAttendance() {
        return autoPublishAttendance;
    }

    public void setAutoPublishAttendance(Boolean autoPublishAttendance) {
        this.autoPublishAttendance = autoPublishAttendance;
    }
}
