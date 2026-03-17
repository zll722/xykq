# 前端路由与菜单结构（Vue）

## 1. 前端目录建议

```text
src/
  api/
    auth.ts
    user.ts
    class.ts
    course.ts
    attendance.ts
    leave.ts
    stats.ts
    message.ts
    system.ts
  layouts/
    AdminLayout.vue
    UserLayout.vue
  router/
    index.ts
    modules/
      admin.ts
      user.ts
  stores/
    auth.ts
    permission.ts
    app.ts
  views/
    admin/
    user/
    common/
  components/
    charts/
    tables/
    forms/
  utils/
    request.ts
    auth.ts
    constants.ts
```

## 2. 路由设计

## 2.1 公共路由
- `/login`：登录页
- `/register`：注册页（用户）
- `/403`：无权限
- `/404`：未找到

## 2.2 管理员路由（`/admin`）
- `/admin/dashboard`：管理端首页
- `/admin/system/admins`：管理员管理
- `/admin/system/users`：用户管理
- `/admin/system/roles`：角色权限
- `/admin/teaching/classes`：班级管理
- `/admin/teaching/courses`：课程管理
- `/admin/teaching/schedules`：课程排班
- `/admin/attendance/rules`：考勤规则
- `/admin/attendance/records`：考勤记录
- `/admin/attendance/adjustments`：考勤补录
- `/admin/attendance/exceptions`：异常处理
- `/admin/leave/pending`：待审批请假
- `/admin/leave/history`：审批历史
- `/admin/stats/rate`：考勤率统计
- `/admin/stats/abnormal`：异常分析
- `/admin/stats/export`：数据导出
- `/admin/system/config`：系统配置
- `/admin/system/logs`：操作日志
- `/admin/profile`：个人中心

## 2.3 用户路由（`/user`）
- `/user/dashboard`：用户首页
- `/user/courses`：课程列表
- `/user/sign-in`：在线签到
- `/user/attendance/records`：个人考勤记录
- `/user/attendance/calendar`：考勤日历
- `/user/leave/apply`：请假申请
- `/user/leave/progress`：审批进度
- `/user/leave/history`：请假记录
- `/user/messages`：系统消息
- `/user/profile`：个人资料
- `/user/security/password`：修改密码

## 3. 菜单与权限映射建议
- `ADMIN` 角色加载 `admin` 路由模块
- `USER` 角色加载 `user` 路由模块
- 菜单项 `meta.perm` 对应后端 `perm_code`
- 页面按钮级权限通过 `v-permission` 指令控制

示例：
- 菜单权限：`attendance:record:list`
- 按钮权限：`attendance:record:adjust`

## 4. 页面实现优先顺序
1. 登录鉴权、路由守卫、动态菜单
2. 管理端：用户/班级/课程/排班
3. 用户端：课程列表、签到、考勤记录
4. 请假申请与审批链路
5. 统计图表与导出、消息中心

## 5. 状态管理（Pinia）建议
- `authStore`：token、当前用户、登录态
- `permissionStore`：角色、权限码、动态路由
- `appStore`：主题、布局、全局加载状态
- `messageStore`（可选）：未读消息数量

## 6. 关键交互约束
- 签到按钮按时间窗启用/禁用，并显示倒计时
- 请假审批结果与消息中心联动刷新
- 统计筛选条件变化时图表与表格保持一致
- 导出操作采用异步 loading 防止重复提交
