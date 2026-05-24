package com.campus.attendance.dto.counselor;

public class CounselorClassItem {
    private Long id;
    private String classCode;
    private String className;
    private Integer capacity;
    private Integer studentCount;
    private Integer pendingLeaveCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    public Integer getPendingLeaveCount() { return pendingLeaveCount; }
    public void setPendingLeaveCount(Integer pendingLeaveCount) { this.pendingLeaveCount = pendingLeaveCount; }
}
