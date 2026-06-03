package com.campus.attendance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.campus.attendance.mapper")
public class SmartAttendanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartAttendanceApplication.class, args);
    }
}
