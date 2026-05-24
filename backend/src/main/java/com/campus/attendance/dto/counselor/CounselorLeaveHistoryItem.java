package com.campus.attendance.dto.counselor;

public class CounselorLeaveHistoryItem {
    private Long id;
    private String studentName;
    private String studentNo;
    private String className;
    private String leaveType;
    private String reason;
    private String startTime;
    private String endTime;
    private String status;
    private String submittedAt;
    private String approverName;
    private String actedAt;
    private String comment;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    public String getActedAt() { return actedAt; }
    public void setActedAt(String actedAt) { this.actedAt = actedAt; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
