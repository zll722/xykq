package com.campus.attendance.domain;

import java.time.LocalTime;

public class CourseSchedule {
    private Long id;
    private Long courseId;
    private String courseName;
    private Long classId;
    private String className;
    private Integer weekNo;
    private Integer weekDay;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private Integer attendanceRadius;
    private Boolean autoPublishAttendance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
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
