package com.campus.attendance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.attendance.mapper")
public class SmartAttendanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartAttendanceApplication.class, args);
    }
}
