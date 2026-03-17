package com.campus.attendance.domain;

import java.time.LocalDateTime;

public class LeaveApproval {
    private Long id;
    private Long leaveRequestId;
    private Long approverId;
    private String action;
    private String comment;
    private LocalDateTime actedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLeaveRequestId() {
        return leaveRequestId;
    }

    public void setLeaveRequestId(Long leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getActedAt() {
        return actedAt;
    }

    public void setActedAt(LocalDateTime actedAt) {
        this.actedAt = actedAt;
    }
}
