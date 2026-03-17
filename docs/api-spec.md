# 智能校园考勤系统 API 清单（V1）

## 1. 约定
- Base URL：`/api/v1`
- 认证方式：`Authorization: Bearer <token>`
- 响应结构：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

- 分页参数：`pageNum`、`pageSize`
- 分页返回建议：`records`、`total`、`pageNum`、`pageSize`

## 2. 认证模块 `/auth`

### 2.1 登录
- `POST /auth/login`
- 请求体：`username`、`password`
- 返回：`token`、`userInfo`、`permissions`

### 2.2 注册（用户）
- `POST /auth/register`
- 请求体：`studentNo`、`realName`、`username`、`password`、`classId`

### 2.3 退出
- `POST /auth/logout`

### 2.4 修改密码
- `POST /auth/change-password`
- 请求体：`oldPassword`、`newPassword`

### 2.5 当前用户信息
- `GET /auth/me`

## 3. 用户与权限模块

### 3.1 管理员管理 `/admins`
- `GET /admins`：管理员列表
- `POST /admins`：新增管理员
- `PUT /admins/{id}`：编辑管理员
- `DELETE /admins/{id}`：删除管理员
- `PUT /admins/{id}/roles`：分配角色

### 3.2 用户管理 `/users`
- `GET /users`：用户列表（支持状态、姓名、学号筛选）
- `GET /users/{id}`：用户详情
- `POST /users`：新增用户
- `PUT /users/{id}`：编辑用户
- `PUT /users/{id}/status`：启用/禁用

### 3.3 角色权限 `/roles`
- `GET /roles`
- `POST /roles`
- `PUT /roles/{id}`
- `DELETE /roles/{id}`
- `GET /roles/{id}/permissions`
- `PUT /roles/{id}/permissions`

## 4. 班级与课程模块

### 4.1 班级 `/classes`
- `GET /classes`
- `POST /classes`
- `PUT /classes/{id}`
- `DELETE /classes/{id}`
- `GET /classes/{id}/students`

### 4.2 课程 `/courses`
- `GET /courses`
- `POST /courses`
- `PUT /courses/{id}`
- `DELETE /courses/{id}`

### 4.3 排班 `/schedules`
- `GET /schedules`
- `POST /schedules`
- `PUT /schedules/{id}`
- `DELETE /schedules/{id}`
- `GET /schedules/my`：当前用户课表

## 5. 考勤模块 `/attendance`

### 5.1 规则管理（管理员）
- `GET /attendance/rules`
- `POST /attendance/rules`
- `PUT /attendance/rules/{id}`
- `PUT /attendance/rules/{id}/status`

### 5.2 签到（用户）
- `POST /attendance/sign-in`
- 请求体：`scheduleId`、`attendanceDate`
- 返回：签到结果 `PRESENT/LATE/ABSENT`

### 5.3 记录查询
- `GET /attendance/records`：管理员查询（课程、班级、日期、状态）
- `GET /attendance/records/my`：用户个人记录

### 5.4 补录与异常
- `POST /attendance/records/{id}/adjust`：考勤修正
- `GET /attendance/exceptions`：异常记录列表
- `PUT /attendance/exceptions/{id}/resolve`：确认/修正异常

## 6. 请假审批模块 `/leave`

### 6.1 用户侧
- `POST /leave/requests`：提交请假
- `GET /leave/requests/my`：我的请假记录
- `GET /leave/requests/{id}`：请假详情
- `PUT /leave/requests/{id}/cancel`：撤销申请（待审批时）

### 6.2 管理员侧
- `GET /leave/approvals/pending`：待审批列表
- `POST /leave/approvals/{requestId}`：审批（通过/驳回）
- `GET /leave/approvals/history`：审批历史

## 7. 统计与导出模块 `/stats`

### 7.1 统计分析
- `GET /stats/attendance-rate`：按课程/班级/时间段统计出勤率
- `GET /stats/attendance-abnormal`：迟到/早退/缺勤分布分析
- `GET /stats/my-overview`：个人统计总览
- `GET /stats/my-calendar`：个人考勤日历

### 7.2 导出
- `GET /stats/export/attendance`：导出考勤明细（Excel）
- `GET /stats/export/summary`：导出统计报表（Excel）

## 8. 消息与系统维护模块

### 8.1 消息 `/messages`
- `GET /messages`：消息列表
- `PUT /messages/{id}/read`：标记已读
- `PUT /messages/read-all`：全部已读

### 8.2 系统配置 `/system/config`
- `GET /system/config`
- `PUT /system/config`

### 8.3 操作日志 `/system/logs`
- `GET /system/logs`

## 9. 状态码建议
- `0`：成功
- `4001`：参数校验失败
- `4003`：未授权/Token 无效
- `4004`：无访问权限
- `4009`：重复提交
- `5000`：系统异常

## 10. 联调优先级
1. `auth` 登录与权限菜单
2. `classes/courses/schedules` 主数据
3. `attendance` 签到与记录
4. `leave` 申请审批流程
5. `stats` 图表与导出
