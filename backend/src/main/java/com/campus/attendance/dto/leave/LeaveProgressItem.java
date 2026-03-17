package com.campus.attendance.dto.leave;

public class LeaveProgressItem {
    private Long requestId;
    private String status;
    private String leaveType;
    private String startTime;
    private String endTime;
    private String submittedAt;
    private String latestAction;
    private String latestComment;
    private String latestActedAt;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
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

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getLatestAction() {
        return latestAction;
    }

    public void setLatestAction(String latestAction) {
        this.latestAction = latestAction;
    }

    public String getLatestComment() {
        return latestComment;
    }

    public void setLatestComment(String latestComment) {
        this.latestComment = latestComment;
    }

    public String getLatestActedAt() {
        return latestActedAt;
    }

    public void setLatestActedAt(String latestActedAt) {
        this.latestActedAt = latestActedAt;
    }
}
