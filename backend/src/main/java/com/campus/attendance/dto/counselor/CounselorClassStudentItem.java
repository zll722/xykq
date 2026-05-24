package com.campus.attendance.dto.counselor;

public class CounselorClassStudentItem {
    private Long userId;
    private String studentNo;
    private String realName;
    private String phone;
    private Integer presentCount;
    private Integer absentCount;
    private Integer lateCount;
    private Integer leaveCount;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getPresentCount() { return presentCount; }
    public void setPresentCount(Integer presentCount) { this.presentCount = presentCount; }
    public Integer getAbsentCount() { return absentCount; }
    public void setAbsentCount(Integer absentCount) { this.absentCount = absentCount; }
    public Integer getLateCount() { return lateCount; }
    public void setLateCount(Integer lateCount) { this.lateCount = lateCount; }
    public Integer getLeaveCount() { return leaveCount; }
    public void setLeaveCount(Integer leaveCount) { this.leaveCount = leaveCount; }
}
