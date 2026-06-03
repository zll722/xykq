package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.dto.admin.AdminAttendanceRecordItem;
import com.campus.attendance.dto.stats.AttendanceAbnormalStats;
import com.campus.attendance.dto.stats.AttendanceRateStats;
import com.campus.attendance.security.RequirePermission;
import com.campus.attendance.service.StatsService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminStatsController {
    private final StatsService statsService;

    public AdminStatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/attendance/records")
    public ApiResponse<List<AdminAttendanceRecordItem>> listAttendanceRecords(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate attendanceDate,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(statsService.listAdminAttendanceRecords(courseId, classId, attendanceDate, status));
    }

    @GetMapping("/stats/attendance-rate")
    public ApiResponse<AttendanceRateStats> attendanceRate(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return ApiResponse.ok(statsService.attendanceRate(courseId, classId, startDate, endDate));
    }

    @GetMapping("/stats/attendance-abnormal")
    public ApiResponse<AttendanceAbnormalStats> attendanceAbnormal(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return ApiResponse.ok(statsService.abnormalStats(courseId, classId, startDate, endDate));
    }

    @GetMapping("/stats/export/attendance")
    @RequirePermission("stats:export")
    public void exportAttendance(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletResponse response
    ) throws IOException {
        List<AdminAttendanceRecordItem> list = statsService.listAdminAttendanceRecords(courseId, classId, null, null);
        if (startDate != null || endDate != null) {
            list = list.stream().filter(item -> {
                LocalDate d = LocalDate.parse(item.getAttendanceDate());
                boolean ge = startDate == null || !d.isBefore(startDate);
                boolean le = endDate == null || !d.isAfter(endDate);
                return ge && le;
            }).toList();
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("attendance");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Course");
            header.createCell(2).setCellValue("Class");
            header.createCell(3).setCellValue("StudentId");
            header.createCell(4).setCellValue("Date");
            header.createCell(5).setCellValue("SignInTime");
            header.createCell(6).setCellValue("SignOutTime");
            header.createCell(7).setCellValue("Status");

            for (int i = 0; i < list.size(); i++) {
                AdminAttendanceRecordItem item = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getCourseName());
                row.createCell(2).setCellValue(item.getClassName());
                row.createCell(3).setCellValue(item.getStudentId());
                row.createCell(4).setCellValue(item.getAttendanceDate());
                row.createCell(5).setCellValue(item.getSignInTime() == null ? "" : item.getSignInTime());
                row.createCell(6).setCellValue(item.getSignOutTime() == null ? "" : item.getSignOutTime());
                row.createCell(7).setCellValue(item.getStatus());
            }

            String filename = URLEncoder.encode("attendance_export.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        }
    }

    @GetMapping("/stats/export/summary")
    @RequirePermission("stats:export")
    public void exportSummary(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletResponse response
    ) throws IOException {
        AttendanceRateStats rate = statsService.attendanceRate(courseId, classId, startDate, endDate);
        AttendanceAbnormalStats abnormal = statsService.abnormalStats(courseId, classId, startDate, endDate);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("summary");
            int rowNum = 0;
            Row r0 = sheet.createRow(rowNum++);
            r0.createCell(0).setCellValue("Metric");
            r0.createCell(1).setCellValue("Value");

            Row r1 = sheet.createRow(rowNum++);
            r1.createCell(0).setCellValue("Total Records");
            r1.createCell(1).setCellValue(rate.getTotal());
            Row r2 = sheet.createRow(rowNum++);
            r2.createCell(0).setCellValue("Present Rate (%)");
            r2.createCell(1).setCellValue(rate.getPresentRate());
            Row r3 = sheet.createRow(rowNum++);
            r3.createCell(0).setCellValue("Late Count");
            r3.createCell(1).setCellValue(abnormal.getLateCount());
            Row r4 = sheet.createRow(rowNum++);
            r4.createCell(0).setCellValue("Absent Count");
            r4.createCell(1).setCellValue(abnormal.getAbsentCount());
            Row r5 = sheet.createRow(rowNum++);
            r5.createCell(0).setCellValue("Leave Count");
            r5.createCell(1).setCellValue(abnormal.getLeaveCount());
            Row r6 = sheet.createRow(rowNum++);
            r6.createCell(0).setCellValue("Early Leave Count");
            r6.createCell(1).setCellValue(abnormal.getEarlyLeaveCount());

            String filename = URLEncoder.encode("attendance_summary.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        }
    }
}
