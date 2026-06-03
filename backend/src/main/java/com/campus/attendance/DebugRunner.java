package com.campus.attendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DebugRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DEBUG_RUNNER_START");
        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM attendance_record ORDER BY id DESC LIMIT 10");
        for (Map<String, Object> r : records) {
            System.out.println("RECORD: " + r);
        }
        System.out.println("DEBUG_RUNNER_END");
    }
}
