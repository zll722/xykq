# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

智能校园考勤系统，支持四种角色：管理员(ADMIN)、教师(TEACHER)、辅导员(COUNSELOR)、学生(USER)。包含三个子项目：Spring Boot 后端、Vue 3 Web 前端、微信小程序端。

## 技术栈

- **后端**：Spring Boot 3.3.5 (JDK 17)、MyBatis 3.0.4、MySQL 8、JJWT 0.12.6
- **前端**：Vue 3、Vite 6、Pinia 3、Vue Router 4、Element Plus、ECharts、Axios
- **小程序**：微信原生（AppID: wx2c6c8ca122440e03）

## 构建与运行命令

### 数据库初始化
```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p smart_attendance < sql/test-data.sql
```
默认连接：`127.0.0.1:3306/smart_attendance`，用户名 `root`，密码 `root`。

初始账号：admin/123456、student001/123456。

### 后端
```bash
cd backend
mvn spring-boot:run             # 开发启动，端口 8080
mvn clean package               # 打包 JAR
mvn test                        # 全部单元测试
mvn test -Dtest=XxxTest         # 单个测试类
mvn test -Dtest=XxxTest#method  # 单个测试方法
```

### 前端
```bash
cd frontend
npm install
npm run dev      # 开发服务器，端口 5173
npm run build    # 生产构建
npm run test     # Vitest 单元测试
npm run e2e      # Playwright E2E 测试
```

### 小程序
用微信开发者工具打开 `miniprogram/` 目录，后端地址配置在 `miniprogram/app.js` 的 `globalData.baseUrl`。

## 代码架构

### 后端包结构 (`backend/src/main/java/com/campus/attendance/`)

| 包 | 职责 |
|---|---|
| `controller/` | REST 控制器，按角色分组，统一返回 `ApiResponse<T>` |
| `service/` | 业务逻辑，接口+实现分离（`service/impl/`） |
| `mapper/` | MyBatis Mapper 接口，对应 `resources/mapper/*.xml` |
| `domain/` | 实体类（Lombok `@Data`） |
| `dto/` | 请求/响应 DTO，按功能子目录组织（auth/、attendance/、leave/、admin/、stats/） |
| `security/` | JWT 过滤器（JwtAuthenticationFilter）和 JWT 工具（JwtProvider） |
| `aspect/` | AOP 切面：OperationLogAspect（审计日志）、PermissionGuardAspect（权限校验） |
| `config/` | SecurityConfig、WebMvcConfig |
| `exception/` | BizException + 全局异常处理器 |
| `util/` | SecurityUtils（从 JWT 提取当前用户 ID） |

### 前端目录结构 (`frontend/src/`)

| 目录 | 职责 |
|---|---|
| `router/modules/` | 按角色分拆路由（admin.js、user.js、teacher.js、counselor.js） |
| `stores/auth.js` | Pinia 认证 store，持久化 token/userInfo/permissions 至 localStorage |
| `api/` | Axios 请求封装，按功能模块分文件 |
| `directives/permission.js` | 自定义指令 `v-permission="['code']"`，按权限码控制 UI 渲染 |
| `views/` | 页面组件，按角色目录组织 |
| `layouts/` | 各角色布局组件 |

## 关键架构决策

### 无状态 JWT 认证
后端不使用 HttpSession，每次请求通过 `SecurityUtils.getCurrentUserId()` 从 JWT 中提取用户身份。Token 有效期 12 小时（`security.jwt.expire-hours`）。登出仅清除客户端 localStorage，服务端不做 token 吊销，已签发 token 在到期前始终有效。

### 扁平权限码 RBAC
权限以字符串码（如 `attendance_view`、`leave_approve`）存储，角色通过 `sys_role_permission` 表关联权限码。后端用 `@RequirePermission("xxx")` 注解由 AOP 拦截校验，前端用 `v-permission` 指令和 `/api/v1/auth/permissions` 接口控制 UI 可见性。

### 考勤状态实时计算（不落库）
`attendance_record` 表只存原始签到时间，PRESENT/LATE/ABSENT 状态在查询时根据课程时间 + `AttendanceRule` 的阈值配置动态计算。修改 AttendanceRule 会回溯影响历史记录的显示状态。

### 文件上传到本地文件系统
上传文件存储在 `../uploads/`（项目根目录同级），路径由 `app.upload.base-dir` 配置。生产环境需迁移至 OSS/S3。

### 审计日志 AOP
关键操作（登录、审批、删除）由 `OperationLogAspect` 拦截写入 `operation_log` 表，新增需审计的操作须添加 `@OperationLog` 注解。

## 核心数据表

| 表 | 说明 |
|---|---|
| `sys_user` | 用户账号（含角色） |
| `sys_role / sys_permission / sys_role_permission` | RBAC 三表 |
| `student_profile / class_info` | 学生档案与班级 |
| `course_info / course_schedule` | 课程信息与排课 |
| `attendance_rule / attendance_record / attendance_adjustment` | 考勤规则、记录与人工修正 |
| `leave_request / leave_approval` | 请假申请与审批 |
| `notify_message / operation_log / system_config` | 通知、审计日志、系统配置 |

## 新增功能开发流程

1. `sql/schema.sql` 加表 → `domain/` 加实体 → `mapper/` 加 Mapper 接口与 XML
2. `service/` 定义接口 → `service/impl/` 实现（`@Service`，构造器注入）
3. `controller/` 加控制器，写操作加 `@RequirePermission`，返回 `ApiResponse.ok(data)`
4. `frontend/src/api/` 加 API 封装 → `views/` 加页面 → `router/modules/` 加路由（含 `meta.role`）
5. 需要权限控制的按钮加 `v-permission="['xxx_view']"`

## 调试技巧

开启 MyBatis SQL 日志，在 `application.yml` 添加：
```yaml
logging:
  level:
    com.campus.attendance.mapper: debug
```
