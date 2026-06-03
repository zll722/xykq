package com.campus.attendance.job;

import com.campus.attendance.domain.AttendanceSession;
import com.campus.attendance.domain.CourseSchedule;
import com.campus.attendance.mapper.AttendanceSessionMapper;
import com.campus.attendance.mapper.ScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AttendancePublishJob {

    private static final Logger log = LoggerFactory.getLogger(AttendancePublishJob.class);

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private AttendanceSessionMapper sessionMapper;

    // Run every minute
    @Scheduled(cron = "0 * * * * ?")
    public void autoPublishAttendance() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();
        int currentWeekDay = today.getDayOfWeek().getValue();

        List<CourseSchedule> schedules = scheduleMapper.findByWeekDay(currentWeekDay);

        for (CourseSchedule schedule : schedules) {
            if (Boolean.TRUE.equals(schedule.getAutoPublishAttendance())) {
                // Check for SIGN_IN session (e.g. within 15 mins before start time)
                long minsToStart = ChronoUnit.MINUTES.between(currentTime, schedule.getStartTime());
                if (minsToStart >= 0 && minsToStart <= 15) {
                    publishSessionIfNotExists(schedule, today, "SIGN_IN", now);
                }

                // Check for SIGN_OUT session (e.g. within 15 mins before end time)
                long minsToEnd = ChronoUnit.MINUTES.between(currentTime, schedule.getEndTime());
                if (minsToEnd >= -15 && minsToEnd <= 15) {
                    publishSessionIfNotExists(schedule, today, "SIGN_OUT", now);
                }
            }
        }
    }

    private void publishSessionIfNotExists(CourseSchedule schedule, LocalDate date, String signType, LocalDateTime now) {
        AttendanceSession existing = sessionMapper.findActiveSession(schedule.getId(), date, signType);
        if (existing == null) {
            AttendanceSession session = new AttendanceSession();
            session.setScheduleId(schedule.getId());
            session.setCourseId(schedule.getCourseId());
            session.setClassId(schedule.getClassId());
            session.setAttendanceDate(date);
            session.setSignType(signType);
            session.setStatus("OPEN");
            session.setStartTime(now);
            // set end time to 30 mins later
            session.setEndTime(now.plusMinutes(30));
            sessionMapper.insert(session);
            log.info("Auto published {} attendance for schedule {}", signType, schedule.getId());
        }
    }
}
