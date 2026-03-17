package com.campus.attendance.dto.stats;

public class AttendanceAbnormalStats {
    private long total;
    private long lateCount;
    private long absentCount;
    private long leaveCount;
    private long earlyLeaveCount;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getLateCount() {
        return lateCount;
    }

    public void setLateCount(long lateCount) {
        this.lateCount = lateCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(long absentCount) {
        this.absentCount = absentCount;
    }

    public long getLeaveCount() {
        return leaveCount;
    }

    public void setLeaveCount(long leaveCount) {
        this.leaveCount = leaveCount;
    }

    public long getEarlyLeaveCount() {
        return earlyLeaveCount;
    }

    public void setEarlyLeaveCount(long earlyLeaveCount) {
        this.earlyLeaveCount = earlyLeaveCount;
    }
}
