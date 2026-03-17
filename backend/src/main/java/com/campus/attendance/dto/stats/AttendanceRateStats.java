package com.campus.attendance.dto.stats;

public class AttendanceRateStats {
    private long total;
    private long presentCount;
    private long lateCount;
    private long absentCount;
    private double presentRate;
    private double lateRate;
    private double absentRate;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
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

    public double getLateRate() {
        return lateRate;
    }

    public void setLateRate(double lateRate) {
        this.lateRate = lateRate;
    }

    public double getAbsentRate() {
        return absentRate;
    }

    public void setAbsentRate(double absentRate) {
        this.absentRate = absentRate;
    }
}
