package com.campus.attendance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class DebugRunnerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testPrintRecords() {
        System.out.println("DEBUG_RUNNER_START");
        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM attendance_record ORDER BY id DESC LIMIT 10");
        for (Map<String, Object> r : records) {
            System.out.println("RECORD: " + r);
        }
        
        List<Map<String, Object>> schedules = jdbcTemplate.queryForList("SELECT * FROM course_schedule ORDER BY id DESC LIMIT 10");
        for (Map<String, Object> s : schedules) {
            System.out.println("SCHEDULE: " + s);
        }

        System.out.println("DEBUG_RUNNER_END");
    }
}
