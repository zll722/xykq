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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class AttendanceJob {

    private static final Logger log = LoggerFactory.getLogger(AttendanceJob.class);

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private AttendanceSessionMapper attendanceSessionMapper;

    // Run every minute at 0 seconds
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void autoPublishAttendanceSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime().withSecond(0).withNano(0);
        int weekDay = today.getDayOfWeek().getValue();

        log.info("Running autoPublishAttendanceSessions at {}", now);

        List<CourseSchedule> todaySchedules = scheduleMapper.findByWeekDay(weekDay);

        for (CourseSchedule schedule : todaySchedules) {
            // Check for SIGN_IN: 5 mins before start
            LocalTime signInPublishTime = schedule.getStartTime().minusMinutes(5);
            if (currentTime.equals(signInPublishTime) || (currentTime.isAfter(signInPublishTime) && currentTime.isBefore(schedule.getStartTime()))) {
                publishSessionIfNotExist(schedule, today, "SIGN_IN", now);
            }

            // Check for SIGN_OUT: exactly at end time or 5 mins after
            // Let's say we publish sign out at the end time and keep it open for 15 mins
            // For the job, we publish it exactly at end time.
            LocalTime signOutPublishTime = schedule.getEndTime();
            if (currentTime.equals(signOutPublishTime) || (currentTime.isAfter(signOutPublishTime) && currentTime.isBefore(schedule.getEndTime().plusMinutes(5)))) {
                publishSessionIfNotExist(schedule, today, "SIGN_OUT", now);
            }
        }
    }

    private void publishSessionIfNotExist(CourseSchedule schedule, LocalDate today, String signType, LocalDateTime now) {
        AttendanceSession existingSession = attendanceSessionMapper.findActiveSession(schedule.getId(), today, signType);
        if (existingSession == null) {
            // Might exist but closed. Need to check if any session exists for this type.
            // Let's just create a new one if NO session exists for this schedule, date and type at all.
            List<AttendanceSession> sessions = attendanceSessionMapper.findSessionsByScheduleAndDate(schedule.getId(), today);
            boolean exists = sessions.stream().anyMatch(s -> s.getSignType().equals(signType));
            
            if (!exists) {
                AttendanceSession session = new AttendanceSession();
                session.setScheduleId(schedule.getId());
                session.setCourseId(schedule.getCourseId());
                session.setClassId(schedule.getClassId());
                session.setAttendanceDate(today);
                session.setSignType(signType);
                session.setStatus("OPEN");
                session.setStartTime(now);
                // End time can be null or a fixed duration. We'll leave it null until teacher closes it, or auto-close it later.
                attendanceSessionMapper.insert(session);
                log.info("Auto-published {} session for schedule {}", signType, schedule.getId());
            }
        }
    }
}
