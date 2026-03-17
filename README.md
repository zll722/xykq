# 基于 Spring Boot + Vue 的智能校园考勤系统

## 1. 项目简介
本项目面向高校日常教学场景，构建一个支持**管理员**与**用户（学生）**双角色的智能考勤系统。
系统覆盖课程排班、在线签到、请假审批、考勤统计分析、异常处理与消息通知等核心流程，提升教学管理效率与考勤数据准确性。

## 2. 技术栈与运行环境
- 后端：Spring Boot（JDK 17）
- 持久层：MyBatis
- 前端：Vue（建议 Vue 3 + Vite）
- 数据库：MySQL 8
- Node.js：22
- 字符编码：UTF-8
- 权限模型：RBAC（管理员、用户）

## 3. 角色与功能需求

### 3.1 管理员功能

#### 3.1.1 系统用户管理
- 登录、修改密码
- 管理员用户管理：新增、编辑、删除管理员账号，分配权限
- 用户管理：查看用户列表，管理用户状态，编辑用户信息

#### 3.1.2 课程班级管理
- 班级管理：新增、编辑、删除班级信息，设置班级人数和班主任
- 课程管理：新增、编辑、删除课程信息，设置上课时间、地点和授课教师
- 课程排班管理：配置课程与班级关联，设置每周上课安排

#### 3.1.3 考勤管理
- 考勤规则设置：签到时间范围、迟到阈值、缺勤判定规则
- 考勤记录管理：按课程、班级、日期、状态筛选查询
- 考勤补录：手动录入/修改学生考勤记录，填写补录原因
- 考勤异常处理：查看异常记录并确认或修正

#### 3.1.4 请假审批管理
- 查看待审批请假列表（按提交时间、请假类型筛选）
- 审批操作：通过/驳回并填写审批意见
- 查看审批历史记录（支持搜索）

#### 3.1.5 数据统计与分析
- 考勤率统计：按课程、班级、时间段统计并可视化
- 考勤异常分析：统计迟到、早退、缺勤分布并输出预警
- 数据导出：导出考勤记录与统计数据（Excel）

#### 3.1.6 系统维护
- 修改密码、维护系统基础配置
- 查看操作日志

### 3.2 用户功能

#### 3.2.1 基础操作
- 用户注册：填写学号、姓名、班级等信息
- 用户登录：账号密码登录，支持登录状态保持
- 修改密码：验证旧密码并设置新密码
- 完善个人信息：头像、联系方式等

#### 3.2.2 课程与签到
- 查看课程列表：个人课程安排、上课时间地点
- 在线签到：在规则时间内打卡并返回签到状态
- 查看考勤记录：按课程查看出勤、迟到、缺勤详情

#### 3.2.3 请假申请
- 提交请假申请：类型、原因、起止时间、证明材料
- 查看审批进度：实时状态跟踪
- 查看请假记录：历史记录与审批结果

#### 3.2.4 个人中心
- 考勤统计总览：出勤率、迟到次数、缺勤次数
- 考勤日历：按日期展示个人考勤状态
- 系统消息通知：签到提醒、审批结果、异常提醒

## 4. 业务模块划分
- 认证与权限模块（登录、JWT、角色权限）
- 用户与管理员模块
- 班级与课程模块
- 课程排班模块
- 考勤规则与签到模块
- 考勤记录与异常处理模块
- 请假申请与审批模块
- 统计分析与导出模块
- 消息通知模块
- 操作日志与系统配置模块

## 5. 数据库设计（核心表示例）
- `sys_user`：用户基础信息（账号、密码、角色、状态）
- `sys_role`、`sys_permission`、`sys_role_permission`：权限模型
- `student_profile`：学号、班级、联系方式等扩展信息
- `class_info`：班级信息
- `course_info`：课程信息
- `course_schedule`：课程-班级-周次-时间段排班
- `attendance_rule`：考勤规则配置
- `attendance_record`：签到与考勤状态记录
- `attendance_adjustment`：考勤补录/修正记录
- `leave_request`：请假申请
- `leave_approval`：审批记录
- `notify_message`：系统通知
- `operation_log`：后台操作日志

## 6. 后端实现计划（Spring Boot + MyBatis）

### 阶段 1：项目基础搭建
- 初始化 Spring Boot 工程（分层：controller/service/mapper/domain）
- 集成 MyBatis、MySQL、参数校验、统一返回体与全局异常处理
- 接入 JWT 鉴权与 RBAC 权限控制
- 建立基础审计字段（创建人、创建时间、更新人、更新时间）

### 阶段 2：主数据模块
- 完成用户、管理员、角色权限接口
- 完成班级、课程、课程排班 CRUD
- 完成操作日志记录切面（登录、审批、删除、导出等关键操作）

### 阶段 3：核心业务模块
- 实现考勤规则配置与规则计算服务
- 实现在线签到接口（时间窗口、重复签到校验、状态判定）
- 实现考勤记录查询、补录与异常处理
- 实现请假申请、审批流转、状态机管理

### 阶段 4：统计与导出
- 实现课程/班级/时间段维度统计接口
- 实现异常分布分析接口与预警数据结构
- 实现 Excel 导出（按筛选条件导出明细与统计）

### 阶段 5：稳定性与上线准备
- 补齐单元测试、集成测试、接口文档（OpenAPI/Swagger）
- 增加限流、防重复提交、敏感操作审计
- 准备部署脚本与环境配置（dev/test/prod）

## 7. 前端实现计划（Vue）

### 阶段 1：工程与基础能力
- 初始化 Vue 工程（Vue 3 + Vite + Vue Router + Pinia）
- 封装请求层（Axios）、鉴权拦截器、统一错误处理
- 完成登录页、路由守卫、动态菜单权限渲染

### 阶段 2：管理员端页面
- 用户/管理员管理页面（列表、搜索、弹窗表单）
- 班级、课程、排班管理页面
- 考勤规则、考勤记录、补录与异常处理页面
- 请假审批列表与历史页面
- 数据统计图表页与导出交互
- 系统配置与操作日志页

### 阶段 3：用户端页面
- 注册、登录、个人资料与密码修改
- 课程列表与签到页
- 个人考勤记录页与考勤日历页
- 请假申请、进度查询、历史记录页
- 消息通知中心

### 阶段 4：体验与质量
- 表单校验、空状态、加载态、异常态统一
- 响应式适配（桌面优先，兼顾移动端）
- 前端单测与关键流程 E2E 测试（登录、签到、请假审批）

## 8. 接口与联调建议
- API 按业务域分组：`/auth`、`/users`、`/classes`、`/courses`、`/attendance`、`/leave`、`/stats`
- 统一响应结构：`code`、`message`、`data`
- 统一分页参数：`pageNum`、`pageSize`
- 联调顺序建议：登录鉴权 -> 主数据 -> 签到 -> 请假 -> 统计导出

## 9. 里程碑建议（示例）
- 第 1 周：基础框架、认证授权、用户与权限模块
- 第 2 周：班级课程排班、考勤规则与签到
- 第 3 周：请假审批、考勤异常处理、消息通知
- 第 4 周：统计分析、Excel 导出、测试与部署

## 10. 验收要点
- 角色权限边界清晰，关键接口有鉴权与审计
- 签到状态判定准确（正常/迟到/缺勤）
- 请假审批流状态可追踪且可回溯
- 统计结果与明细数据一致，导出文件可用
- 核心流程在高并发下稳定，异常处理可观测

## 11. 已落地开发文档
- 数据库建表脚本：`sql/schema.sql`
- 后端 API 清单：`docs/api-spec.md`
- 前端路由与菜单设计：`docs/frontend-structure.md`

## 12. 已落地工程骨架
- 后端工程：`backend`（Spring Boot + MyBatis + JWT）
- 前端工程：`frontend`（Vue 3 + Vite + Pinia + Router）

## 13. 快速启动
1. 初始化数据库
   - 在 MySQL 8 执行：`sql/schema.sql`
   - 默认会创建库：`smart_attendance`
   - 若你之前已初始化过旧版本表结构，建议先备份后重建数据库再执行脚本，保证索引与初始化数据一致
   - 初始化账号：
     - 管理员：`admin / 123456`
     - 用户：`student001 / 123456`
   - 脚本同时会初始化：默认班级、默认课程、默认排班、默认考勤规则
2. 启动后端
   - 进入目录：`backend`
   - 修改配置：`src/main/resources/application.yml`（数据库账号密码）
   - 启动命令：`mvn spring-boot:run`
3. 启动前端
   - 进入目录：`frontend`
   - 安装依赖：`npm install`
   - 启动命令：`npm run dev`
4. 联调地址
   - 后端：`http://localhost:8080`
   - 前端：`http://localhost:5173`
5. 文件上传存储
   - 上传接口：`POST /api/v1/files/upload`
   - 默认落盘目录：项目根目录下 `uploads/`
   - 可通过 `backend/src/main/resources/application.yml` 的 `app.upload.base-dir` 调整

## 14. 当前已实现接口（首批）
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/change-password`
- `GET /api/v1/auth/me`
- `GET /api/v1/auth/permissions`
- `GET /api/v1/users`
- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`
- `GET /api/v1/admin/ping`（仅 ADMIN）
- `GET /api/v1/admin/users`
- `POST /api/v1/admin/users`
- `PUT /api/v1/admin/users/{id}`
- `PUT /api/v1/admin/users/{id}/status`
- `DELETE /api/v1/admin/users/{id}`
- `GET /api/v1/admin/roles`
- `POST /api/v1/admin/roles`
- `PUT /api/v1/admin/roles/{id}`
- `DELETE /api/v1/admin/roles/{id}`
- `GET /api/v1/admin/roles/permissions/all`
- `GET /api/v1/admin/roles/{id}/permissions`
- `PUT /api/v1/admin/roles/{id}/permissions`
- `GET /api/v1/admin/classes`
- `POST /api/v1/admin/classes`
- `PUT /api/v1/admin/classes/{id}`
- `DELETE /api/v1/admin/classes/{id}`
- `GET /api/v1/admin/courses`
- `POST /api/v1/admin/courses`
- `PUT /api/v1/admin/courses/{id}`
- `DELETE /api/v1/admin/courses/{id}`
- `GET /api/v1/admin/schedules`
- `POST /api/v1/admin/schedules`
- `PUT /api/v1/admin/schedules/{id}`
- `DELETE /api/v1/admin/schedules/{id}`
- `GET /api/v1/admin/attendance/records`
- `GET /api/v1/admin/attendance/exceptions`
- `GET /api/v1/admin/attendance/rules`
- `POST /api/v1/admin/attendance/rules`
- `PUT /api/v1/admin/attendance/rules/{id}`
- `PUT /api/v1/admin/attendance/rules/{id}/status`
- `POST /api/v1/admin/attendance/records/{id}/adjust`
- `PUT /api/v1/admin/attendance/exceptions/{id}/resolve`
- `GET /api/v1/admin/stats/attendance-rate`
- `GET /api/v1/admin/stats/attendance-abnormal`
- `GET /api/v1/admin/stats/export/attendance`
- `GET /api/v1/admin/stats/export/summary`
- `GET /api/v1/admin/system/logs`
- `GET /api/v1/admin/system/config`
- `PUT /api/v1/admin/system/config`
- `POST /api/v1/attendance/sign-in`
- `GET /api/v1/attendance/records/my`
- `GET /api/v1/schedules/my`
- `GET /api/v1/stats/my-overview`
- `GET /api/v1/stats/my-calendar`
- `GET /api/v1/messages`
- `PUT /api/v1/messages/{id}/read`
- `PUT /api/v1/messages/read-all`
- `POST /api/v1/files/upload`
- `POST /api/v1/leave/requests`
- `GET /api/v1/leave/requests/my`
- `GET /api/v1/leave/requests/my-progress`
- `GET /api/v1/leave/requests/{id}`
- `PUT /api/v1/leave/requests/{id}/cancel`
- `GET /api/v1/leave/approvals/pending`
- `POST /api/v1/leave/approvals/{requestId}`
- `GET /api/v1/leave/approvals/history`

## 15. 权限控制说明
- 已实现 RBAC：角色（ADMIN/USER）+ 权限码（`perm_code`）双层控制
- 后端：关键管理员写操作通过 `@RequirePermission` 校验
- 前端：管理员页面按钮通过 `v-permission` 按权限码动态显示
- 登录后返回权限列表，前端会持久化用于刷新后恢复权限视图

## 16. 自动化测试
- 后端单元测试：
  - 命令：`cd backend && mvn test`
  - 覆盖：`AuthServiceImpl`、`AttendanceServiceImpl`、`LeaveServiceImpl`
- 前端单元测试（Vitest）：
  - 命令：`cd frontend && npm run test`
  - 覆盖：鉴权 Store、权限指令、请假申请页面提交流程
- 前端 E2E（Playwright）：
  - 命令：`cd frontend && npm run e2e`
  - 覆盖：用户链路（登录→签到→请假→审批进度）、管理员审批链路
