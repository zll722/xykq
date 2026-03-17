package com.campus.attendance.dto.user;

public class MyOverviewStats {
    private long totalCount;
    private long presentCount;
    private long lateCount;
    private long absentCount;
    private double presentRate;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(long presentCount) {
        this.presentCount = presentCount;
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

    public double getPresentRate() {
        return presentRate;
    }

    public void setPresentRate(double presentRate) {
        this.presentRate = presentRate;
    }
}
