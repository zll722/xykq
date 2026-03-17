-- 智能校园考勤系统 MySQL 8 建表脚本
-- 字符集：utf8mb4，排序规则：utf8mb4_0900_ai_ci

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS smart_attendance
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE smart_attendance;

-- 用户与权限
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE COMMENT '登录账号',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
  real_name VARCHAR(64) NOT NULL COMMENT '姓名',
  role_code VARCHAR(32) NOT NULL COMMENT 'ADMIN/USER',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  phone VARCHAR(20) NULL,
  email VARCHAR(128) NULL,
  avatar_url VARCHAR(255) NULL,
  last_login_at DATETIME NULL,
  created_by BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  INDEX idx_user_role(role_code),
  INDEX idx_user_status(status),
  INDEX idx_user_deleted(deleted)
) COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(32) NOT NULL UNIQUE,
  role_name VARCHAR(64) NOT NULL,
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  perm_code VARCHAR(64) NOT NULL UNIQUE,
  perm_name VARCHAR(128) NOT NULL,
  perm_type VARCHAR(16) NOT NULL COMMENT 'MENU/BUTTON/API',
  path VARCHAR(255) NULL,
  method VARCHAR(16) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='权限表';

CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_perm(role_id, permission_id),
  CONSTRAINT fk_role_perm_role FOREIGN KEY (role_id) REFERENCES sys_role(id),
  CONSTRAINT fk_role_perm_perm FOREIGN KEY (permission_id) REFERENCES sys_permission(id)
) COMMENT='角色权限关联表';

-- 学生扩展信息
CREATE TABLE IF NOT EXISTS student_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  student_no VARCHAR(32) NOT NULL UNIQUE COMMENT '学号',
  class_id BIGINT NULL,
  grade VARCHAR(32) NULL,
  contact_addr VARCHAR(255) NULL,
  emergency_contact VARCHAR(64) NULL,
  emergency_phone VARCHAR(20) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT='学生档案表';

-- 班级与课程
CREATE TABLE IF NOT EXISTS class_info (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  class_code VARCHAR(32) NOT NULL UNIQUE,
  class_name VARCHAR(64) NOT NULL,
  head_teacher_id BIGINT NULL COMMENT '班主任用户ID',
  capacity INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_class_teacher FOREIGN KEY (head_teacher_id) REFERENCES sys_user(id)
) COMMENT='班级表';

CREATE TABLE IF NOT EXISTS course_info (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_code VARCHAR(32) NOT NULL UNIQUE,
  course_name VARCHAR(128) NOT NULL,
  teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
  location VARCHAR(128) NULL,
  week_day TINYINT NOT NULL COMMENT '1-7',
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES sys_user(id),
  INDEX idx_course_weekday(week_day),
  INDEX idx_course_teacher(teacher_id)
) COMMENT='课程表';

CREATE TABLE IF NOT EXISTS course_schedule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  class_id BIGINT NOT NULL,
  week_no INT NULL COMMENT '学期周次，可空表示全学期',
  week_day TINYINT NOT NULL COMMENT '1-7',
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  location VARCHAR(128) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_schedule_course FOREIGN KEY (course_id) REFERENCES course_info(id),
  CONSTRAINT fk_schedule_class FOREIGN KEY (class_id) REFERENCES class_info(id),
  UNIQUE KEY uk_schedule(course_id, class_id, week_no, week_day, start_time, end_time),
  INDEX idx_schedule_course_class(course_id, class_id),
  INDEX idx_schedule_week(week_day, start_time)
) COMMENT='课程排班表';

-- 考勤
CREATE TABLE IF NOT EXISTS attendance_rule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  rule_name VARCHAR(64) NOT NULL,
  sign_in_start_offset_min INT NOT NULL DEFAULT -15 COMMENT '签到开始: 相对课程开始分钟',
  sign_in_end_offset_min INT NOT NULL DEFAULT 15 COMMENT '签到结束: 相对课程开始分钟',
  late_threshold_min INT NOT NULL DEFAULT 10 COMMENT '迟到阈值分钟',
  absent_threshold_min INT NOT NULL DEFAULT 30 COMMENT '缺勤阈值分钟',
  allow_makeup TINYINT NOT NULL DEFAULT 1,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='考勤规则表';

CREATE TABLE IF NOT EXISTS attendance_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  schedule_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  class_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL COMMENT 'sys_user.id',
  attendance_date DATE NOT NULL,
  signed_at DATETIME NULL,
  status VARCHAR(16) NOT NULL COMMENT 'PRESENT/LATE/LEAVE/EARLY_LEAVE/ABSENT',
  source VARCHAR(16) NOT NULL DEFAULT 'AUTO' COMMENT 'AUTO/MANUAL',
  makeup_reason VARCHAR(255) NULL,
  operator_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_attendance_once(schedule_id, student_id, attendance_date),
  CONSTRAINT fk_att_record_schedule FOREIGN KEY (schedule_id) REFERENCES course_schedule(id),
  CONSTRAINT fk_att_record_course FOREIGN KEY (course_id) REFERENCES course_info(id),
  CONSTRAINT fk_att_record_class FOREIGN KEY (class_id) REFERENCES class_info(id),
  CONSTRAINT fk_att_record_student FOREIGN KEY (student_id) REFERENCES sys_user(id),
  INDEX idx_att_query(course_id, class_id, attendance_date, status),
  INDEX idx_att_student(student_id, attendance_date)
) COMMENT='考勤记录表';

CREATE TABLE IF NOT EXISTS attendance_adjustment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  attendance_record_id BIGINT NOT NULL,
  before_status VARCHAR(16) NOT NULL,
  after_status VARCHAR(16) NOT NULL,
  reason VARCHAR(255) NOT NULL,
  operator_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_att_adj_record FOREIGN KEY (attendance_record_id) REFERENCES attendance_record(id),
  CONSTRAINT fk_att_adj_operator FOREIGN KEY (operator_id) REFERENCES sys_user(id),
  INDEX idx_att_adj_record(attendance_record_id)
) COMMENT='考勤补录修正记录表';

-- 请假审批
CREATE TABLE IF NOT EXISTS leave_request (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  leave_type VARCHAR(32) NOT NULL COMMENT '事假/病假/其他',
  reason VARCHAR(255) NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  proof_url VARCHAR(255) NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/CANCELED',
  submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_leave_student FOREIGN KEY (student_id) REFERENCES sys_user(id),
  INDEX idx_leave_status(status, submitted_at),
  INDEX idx_leave_student(student_id, submitted_at)
) COMMENT='请假申请表';

CREATE TABLE IF NOT EXISTS leave_approval (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  leave_request_id BIGINT NOT NULL,
  approver_id BIGINT NOT NULL,
  action VARCHAR(16) NOT NULL COMMENT 'APPROVED/REJECTED',
  comment VARCHAR(255) NULL,
  acted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_leave_approval_request FOREIGN KEY (leave_request_id) REFERENCES leave_request(id),
  CONSTRAINT fk_leave_approval_user FOREIGN KEY (approver_id) REFERENCES sys_user(id),
  INDEX idx_leave_approval_request(leave_request_id, acted_at)
) COMMENT='请假审批记录表';

-- 通知与日志
CREATE TABLE IF NOT EXISTS notify_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  content VARCHAR(500) NOT NULL,
  msg_type VARCHAR(32) NOT NULL COMMENT 'SIGN_REMIND/LEAVE_RESULT/ATT_EXCEPTION/SYSTEM',
  read_flag TINYINT NOT NULL DEFAULT 0,
  sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  read_at DATETIME NULL,
  CONSTRAINT fk_notify_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  INDEX idx_notify_user_read(user_id, read_flag, sent_at)
) COMMENT='系统消息表';

CREATE TABLE IF NOT EXISTS operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  operator_id BIGINT NULL,
  module_name VARCHAR(64) NOT NULL,
  operation_type VARCHAR(32) NOT NULL,
  request_uri VARCHAR(255) NULL,
  request_method VARCHAR(16) NULL,
  request_params TEXT NULL,
  result_code VARCHAR(32) NULL,
  ip_addr VARCHAR(64) NULL,
  user_agent VARCHAR(255) NULL,
  operated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_op_log_user FOREIGN KEY (operator_id) REFERENCES sys_user(id),
  INDEX idx_op_log_operator(operator_id, operated_at),
  INDEX idx_op_log_module(module_name, operated_at)
) COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS sys_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  config_key VARCHAR(64) NOT NULL UNIQUE,
  config_value VARCHAR(500) NOT NULL,
  config_name VARCHAR(128) NOT NULL,
  remark VARCHAR(255) NULL,
  updated_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='系统配置表';

-- 初始化角色数据
INSERT INTO sys_role (role_code, role_name, remark)
VALUES
  ('ADMIN', '管理员', '系统管理员角色'),
  ('USER', '普通用户', '学生用户角色')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), remark = VALUES(remark);

INSERT INTO sys_permission (perm_code, perm_name, perm_type)
VALUES
  ('user:create', '用户新增', 'BUTTON'),
  ('user:update', '用户编辑', 'BUTTON'),
  ('user:status', '用户状态', 'BUTTON'),
  ('user:delete', '用户删除', 'BUTTON'),
  ('class:create', '班级新增', 'BUTTON'),
  ('class:update', '班级编辑', 'BUTTON'),
  ('class:delete', '班级删除', 'BUTTON'),
  ('course:create', '课程新增', 'BUTTON'),
  ('course:update', '课程编辑', 'BUTTON'),
  ('course:delete', '课程删除', 'BUTTON'),
  ('schedule:create', '排班新增', 'BUTTON'),
  ('schedule:update', '排班编辑', 'BUTTON'),
  ('schedule:delete', '排班删除', 'BUTTON'),
  ('attendance:rule:update', '考勤规则编辑', 'BUTTON'),
  ('attendance:adjust', '考勤补录', 'BUTTON'),
  ('attendance:resolve', '异常处理', 'BUTTON'),
  ('leave:approve', '请假审批', 'BUTTON'),
  ('stats:export', '统计导出', 'BUTTON'),
  ('system:config:update', '系统配置修改', 'BUTTON'),
  ('role:update', '角色权限维护', 'BUTTON')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
INNER JOIN sys_permission p
WHERE r.role_code = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 初始化测试账号（密码均为 123456）
INSERT INTO sys_user (username, password_hash, real_name, role_code, status)
VALUES
  ('admin', '{noop}123456', '系统管理员', 'ADMIN', 1),
  ('student001', '{noop}123456', '测试学生', 'USER', 1)
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name), status = VALUES(status);

-- 初始化基础班级/课程/排班与学生档案
INSERT INTO class_info (class_code, class_name, head_teacher_id, capacity, status, remark)
SELECT 'CLS2026A', '2026级软件工程1班', u.id, 40, 1, '初始化班级'
FROM sys_user u
WHERE u.username = 'admin'
ON DUPLICATE KEY UPDATE class_name = VALUES(class_name), capacity = VALUES(capacity), status = VALUES(status);

INSERT INTO student_profile (user_id, student_no, class_id, grade)
SELECT su.id, '20260001', ci.id, '2026'
FROM sys_user su
INNER JOIN class_info ci ON ci.class_code = 'CLS2026A'
WHERE su.username = 'student001'
ON DUPLICATE KEY UPDATE class_id = VALUES(class_id), grade = VALUES(grade);

INSERT INTO course_info (course_code, course_name, teacher_id, location, week_day, start_time, end_time, status)
SELECT 'CS101', '程序设计基础', u.id, '教学楼A-101', 1, '08:00:00', '09:40:00', 1
FROM sys_user u
WHERE u.username = 'admin'
ON DUPLICATE KEY UPDATE course_name = VALUES(course_name), location = VALUES(location), status = VALUES(status);

INSERT INTO course_schedule (course_id, class_id, week_no, week_day, start_time, end_time, location)
SELECT c.id, ci.id, NULL, 1, '08:00:00', '09:40:00', '教学楼A-101'
FROM course_info c
INNER JOIN class_info ci ON ci.class_code = 'CLS2026A'
WHERE c.course_code = 'CS101'
ON DUPLICATE KEY UPDATE location = VALUES(location);

INSERT INTO attendance_rule (
  rule_name, sign_in_start_offset_min, sign_in_end_offset_min, late_threshold_min, absent_threshold_min, allow_makeup, status
)
SELECT '默认考勤规则', -15, 15, 10, 30, 1, 1
WHERE NOT EXISTS (
  SELECT 1 FROM attendance_rule WHERE rule_name = '默认考勤规则'
);

INSERT INTO notify_message (user_id, title, content, msg_type, read_flag)
SELECT su.id, '系统欢迎', '欢迎使用智能校园考勤系统。请及时完成每日签到。', 'SYSTEM', 0
FROM sys_user su
WHERE su.username = 'student001'
AND NOT EXISTS (
  SELECT 1 FROM notify_message nm WHERE nm.user_id = su.id AND nm.title = '系统欢迎'
);

INSERT INTO sys_config (config_key, config_value, config_name, remark)
VALUES
  ('system_name', '智能校园考勤系统', '系统名称', '前台展示名称'),
  ('sign_remind_enabled', 'true', '签到提醒开关', '是否启用签到提醒'),
  ('default_sign_start_offset_min', '-15', '默认签到开始偏移(分钟)', '课程开始前可签到分钟数'),
  ('default_sign_end_offset_min', '15', '默认签到结束偏移(分钟)', '课程开始后可签到分钟数')
ON DUPLICATE KEY UPDATE
  config_value = VALUES(config_value),
  config_name = VALUES(config_name),
  remark = VALUES(remark);

SET FOREIGN_KEY_CHECKS = 1;
