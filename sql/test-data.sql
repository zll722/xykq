-- 智能校园考勤系统 测试数据填充脚本
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE smart_attendance;

-- 0. 添加角色
INSERT INTO sys_role (role_code, role_name, remark)
VALUES ('TEACHER', '授课教师', '授课教师角色，可查看请假通知和班级考勤')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), remark = VALUES(remark);

INSERT INTO sys_role (role_code, role_name, remark)
VALUES ('COUNSELOR', '辅导员', '辅导员角色，负责审批学生请假、管理所属班级')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), remark = VALUES(remark);

-- 为 TEACHER 角色分配查看类权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'TEACHER'
  AND p.perm_code IN ('leave:view');

-- 为 COUNSELOR 角色分配审批权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'COUNSELOR'
  AND p.perm_code IN ('leave:approve', 'leave:view');

-- 1. 插入更多教师、辅导员和学生用户
INSERT INTO sys_user (username, password_hash, real_name, role_code, status, phone, email) VALUES
('teacher01', '{noop}123456', '李老师', 'TEACHER', 1, '13800138001', 'teacher01@campus.edu'),
('teacher02', '{noop}123456', '王老师', 'TEACHER', 1, '13800138002', 'teacher02@campus.edu'),
('counselor01', '{noop}123456', '陈辅导', 'COUNSELOR', 1, '13700137001', 'counselor01@campus.edu'),
('counselor02', '{noop}123456', '刘辅导', 'COUNSELOR', 1, '13700137002', 'counselor02@campus.edu'),
('student002', '{noop}123456', '张三', 'USER', 1, '13900139002', 'student002@campus.edu'),
('student003', '{noop}123456', '李四', 'USER', 1, '13900139003', 'student003@campus.edu'),
('student004', '{noop}123456', '王五', 'USER', 1, '13900139004', 'student004@campus.edu'),
('student005', '{noop}123456', '赵六', 'USER', 1, '13900139005', 'student005@campus.edu')
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name), role_code = VALUES(role_code);

-- 2. 插入更多班级（head_teacher_id 指向辅导员）
INSERT INTO class_info (class_code, class_name, head_teacher_id, capacity, status, remark)
SELECT 'CLS2026A', '2026级软件工程1班', u.id, 40, 1, '辅导员：陈辅导'
FROM sys_user u WHERE u.username = 'counselor01'
ON DUPLICATE KEY UPDATE head_teacher_id = VALUES(head_teacher_id), remark = VALUES(remark);

INSERT INTO class_info (class_code, class_name, head_teacher_id, capacity, status, remark)
SELECT 'CLS2026B', '2026级软件工程2班', u.id, 40, 1, '辅导员：陈辅导' FROM sys_user u WHERE u.username = 'counselor01'
ON DUPLICATE KEY UPDATE head_teacher_id = VALUES(head_teacher_id), remark = VALUES(remark);

INSERT INTO class_info (class_code, class_name, head_teacher_id, capacity, status, remark)
SELECT 'CLS2026C', '2026级计算机科学1班', u.id, 40, 1, '辅导员：刘辅导' FROM sys_user u WHERE u.username = 'counselor02'
ON DUPLICATE KEY UPDATE head_teacher_id = VALUES(head_teacher_id), remark = VALUES(remark);

-- 3. 补充学生档案
INSERT INTO student_profile (user_id, student_no, class_id, grade)
SELECT u.id, '20260002', c.id, '2026' FROM sys_user u, class_info c WHERE u.username = 'student002' AND c.class_code = 'CLS2026A'
ON DUPLICATE KEY UPDATE class_id = VALUES(class_id);

INSERT INTO student_profile (user_id, student_no, class_id, grade)
SELECT u.id, '20260003', c.id, '2026' FROM sys_user u, class_info c WHERE u.username = 'student003' AND c.class_code = 'CLS2026A'
ON DUPLICATE KEY UPDATE class_id = VALUES(class_id);

INSERT INTO student_profile (user_id, student_no, class_id, grade)
SELECT u.id, '20260004', c.id, '2026' FROM sys_user u, class_info c WHERE u.username = 'student004' AND c.class_code = 'CLS2026B'
ON DUPLICATE KEY UPDATE class_id = VALUES(class_id);

INSERT INTO student_profile (user_id, student_no, class_id, grade)
SELECT u.id, '20260005', c.id, '2026' FROM sys_user u, class_info c WHERE u.username = 'student005' AND c.class_code = 'CLS2026B'
ON DUPLICATE KEY UPDATE class_id = VALUES(class_id);

-- 4. 插入更多课程
INSERT INTO course_info (course_code, course_name, teacher_id, location, week_day, start_time, end_time, status)
SELECT 'CS102', '数据结构与算法', id, '教学楼B-201', 2, '10:00:00', '11:40:00', 1 FROM sys_user WHERE username = 'teacher01'
ON DUPLICATE KEY UPDATE course_name = VALUES(course_name);

INSERT INTO course_info (course_code, course_name, teacher_id, location, week_day, start_time, end_time, status)
SELECT 'CS103', '计算机网络', id, '教学楼C-301', 3, '14:00:00', '15:40:00', 1 FROM sys_user WHERE username = 'teacher02'
ON DUPLICATE KEY UPDATE course_name = VALUES(course_name);

INSERT INTO course_info (course_code, course_name, teacher_id, location, week_day, start_time, end_time, status)
SELECT 'CS104', '操作系统', id, '实验楼D-105', 4, '08:00:00', '09:40:00', 1 FROM sys_user WHERE username = 'admin'
ON DUPLICATE KEY UPDATE course_name = VALUES(course_name);

-- 5. 插入排班
-- 为CLS2026A安排CS102
INSERT IGNORE INTO course_schedule (course_id, class_id, week_no, week_day, start_time, end_time, location)
SELECT co.id, cl.id, NULL, co.week_day, co.start_time, co.end_time, co.location
FROM course_info co, class_info cl WHERE co.course_code = 'CS102' AND cl.class_code = 'CLS2026A';

-- 为CLS2026A安排CS103
INSERT IGNORE INTO course_schedule (course_id, class_id, week_no, week_day, start_time, end_time, location)
SELECT co.id, cl.id, NULL, co.week_day, co.start_time, co.end_time, co.location
FROM course_info co, class_info cl WHERE co.course_code = 'CS103' AND cl.class_code = 'CLS2026A';

-- 为CLS2026B安排CS101和CS104
INSERT IGNORE INTO course_schedule (course_id, class_id, week_no, week_day, start_time, end_time, location)
SELECT co.id, cl.id, NULL, co.week_day, co.start_time, co.end_time, co.location
FROM course_info co, class_info cl WHERE co.course_code = 'CS101' AND cl.class_code = 'CLS2026B';

INSERT IGNORE INTO course_schedule (course_id, class_id, week_no, week_day, start_time, end_time, location)
SELECT co.id, cl.id, NULL, co.week_day, co.start_time, co.end_time, co.location
FROM course_info co, class_info cl WHERE co.course_code = 'CS104' AND cl.class_code = 'CLS2026B';


-- 6. 生成历史考勤数据 (为了让Dashboard有数据)
-- 假设今天是 2026-03-22
-- 插入前几天的考勤记录
INSERT IGNORE INTO attendance_record (schedule_id, course_id, class_id, student_id, attendance_date, signed_at, status, source)
SELECT s.id, s.course_id, s.class_id, u.id, '2026-03-15', '2026-03-15 07:55:00', 'PRESENT', 'AUTO'
FROM course_schedule s
JOIN class_info c ON s.class_id = c.id
JOIN student_profile sp ON sp.class_id = c.id
JOIN sys_user u ON sp.user_id = u.id
WHERE s.week_day = 1 AND c.class_code = 'CLS2026A' AND u.username = 'student001';

INSERT IGNORE INTO attendance_record (schedule_id, course_id, class_id, student_id, attendance_date, signed_at, status, source)
SELECT s.id, s.course_id, s.class_id, u.id, '2026-03-15', '2026-03-15 08:05:00', 'LATE', 'AUTO'
FROM course_schedule s
JOIN class_info c ON s.class_id = c.id
JOIN student_profile sp ON sp.class_id = c.id
JOIN sys_user u ON sp.user_id = u.id
WHERE s.week_day = 1 AND c.class_code = 'CLS2026A' AND u.username = 'student002';

INSERT IGNORE INTO attendance_record (schedule_id, course_id, class_id, student_id, attendance_date, signed_at, status, source)
SELECT s.id, s.course_id, s.class_id, u.id, '2026-03-15', NULL, 'ABSENT', 'AUTO'
FROM course_schedule s
JOIN class_info c ON s.class_id = c.id
JOIN student_profile sp ON sp.class_id = c.id
JOIN sys_user u ON sp.user_id = u.id
WHERE s.week_day = 1 AND c.class_code = 'CLS2026A' AND u.username = 'student003';

INSERT IGNORE INTO attendance_record (schedule_id, course_id, class_id, student_id, attendance_date, signed_at, status, source)
SELECT s.id, s.course_id, s.class_id, u.id, '2026-03-16', '2026-03-16 09:50:00', 'PRESENT', 'AUTO'
FROM course_schedule s
JOIN class_info c ON s.class_id = c.id
JOIN student_profile sp ON sp.class_id = c.id
JOIN sys_user u ON sp.user_id = u.id
WHERE s.week_day = 2 AND c.class_code = 'CLS2026A';

INSERT IGNORE INTO attendance_record (schedule_id, course_id, class_id, student_id, attendance_date, signed_at, status, source)
SELECT s.id, s.course_id, s.class_id, u.id, '2026-03-17', '2026-03-17 13:50:00', 'PRESENT', 'AUTO'
FROM course_schedule s
JOIN class_info c ON s.class_id = c.id
JOIN student_profile sp ON sp.class_id = c.id
JOIN sys_user u ON sp.user_id = u.id
WHERE s.week_day = 3 AND c.class_code = 'CLS2026A' AND u.username != 'student002';

INSERT IGNORE INTO attendance_record (schedule_id, course_id, class_id, student_id, attendance_date, signed_at, status, source)
SELECT s.id, s.course_id, s.class_id, u.id, '2026-03-17', NULL, 'LEAVE', 'MANUAL'
FROM course_schedule s
JOIN class_info c ON s.class_id = c.id
JOIN student_profile sp ON sp.class_id = c.id
JOIN sys_user u ON sp.user_id = u.id
WHERE s.week_day = 3 AND c.class_code = 'CLS2026A' AND u.username = 'student002';

-- 7. 插入请假申请
INSERT IGNORE INTO leave_request (student_id, leave_type, reason, start_time, end_time, status, submitted_at)
SELECT id, '病假', '重感冒发烧，需去医院就诊', '2026-03-17 08:00:00', '2026-03-17 18:00:00', 'APPROVED', '2026-03-16 20:00:00'
FROM sys_user WHERE username = 'student002';

INSERT IGNORE INTO leave_request (student_id, leave_type, reason, start_time, end_time, status, submitted_at)
SELECT id, '事假', '家里有急事需要回去一趟', '2026-03-23 08:00:00', '2026-03-24 18:00:00', 'PENDING', NOW()
FROM sys_user WHERE username = 'student001';

INSERT IGNORE INTO leave_request (student_id, leave_type, reason, start_time, end_time, status, submitted_at)
SELECT id, '公假', '参加省级计算机设计大赛', '2026-03-25 08:00:00', '2026-03-27 18:00:00', 'PENDING', NOW()
FROM sys_user WHERE username = 'student003';

-- 8. 插入系统消息
INSERT IGNORE INTO notify_message (user_id, title, content, msg_type, read_flag, sent_at)
SELECT id, '请假审批通过', '您的病假申请已通过审批。', 'LEAVE_RESULT', 1, '2026-03-16 21:00:00'
FROM sys_user WHERE username = 'student002';

INSERT IGNORE INTO notify_message (user_id, title, content, msg_type, read_flag, sent_at)
SELECT id, '缺勤警告', '您在 2026-03-15 的 程序设计基础 课程中缺勤，请及时说明情况。', 'ATT_EXCEPTION', 0, '2026-03-15 12:00:00'
FROM sys_user WHERE username = 'student003';

SET FOREIGN_KEY_CHECKS = 1;
