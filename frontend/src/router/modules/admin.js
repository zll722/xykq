import AdminLayout from '../../layouts/AdminLayout.vue';
import AdminDashboard from '../../views/admin/AdminDashboard.vue';
import AdminClasses from '../../views/admin/AdminClasses.vue';
import AdminCourses from '../../views/admin/AdminCourses.vue';
import AdminSchedules from '../../views/admin/AdminSchedules.vue';
import AdminLeavePending from '../../views/admin/AdminLeavePending.vue';
import AdminLeaveHistory from '../../views/admin/AdminLeaveHistory.vue';
import AdminAttendanceRecords from '../../views/admin/AdminAttendanceRecords.vue';
import AdminStats from '../../views/admin/AdminStats.vue';
import AdminSystemLogs from '../../views/admin/AdminSystemLogs.vue';
import AdminAttendanceAdjustments from '../../views/admin/AdminAttendanceAdjustments.vue';
import AdminAttendanceExceptions from '../../views/admin/AdminAttendanceExceptions.vue';
import AdminSystemConfig from '../../views/admin/AdminSystemConfig.vue';
import AdminUsers from '../../views/admin/AdminUsers.vue';
import AdminRoles from '../../views/admin/AdminRoles.vue';

export const adminRoutes = [
  {
    path: '/admin',
    component: AdminLayout,
    meta: { role: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        component: AdminDashboard,
        meta: { title: '管理端首页' }
      },
      {
        path: 'classes',
        component: AdminClasses,
        meta: { title: '班级管理' }
      },
      {
        path: 'users',
        component: AdminUsers,
        meta: { title: '用户管理' }
      },
      {
        path: 'roles',
        component: AdminRoles,
        meta: { title: '角色权限' }
      },
      {
        path: 'courses',
        component: AdminCourses,
        meta: { title: '课程管理' }
      },
      {
        path: 'schedules',
        component: AdminSchedules,
        meta: { title: '排班管理' }
      },
      {
        path: 'leave-pending',
        component: AdminLeavePending,
        meta: { title: '请假审批' }
      },
      {
        path: 'leave-history',
        component: AdminLeaveHistory,
        meta: { title: '审批历史' }
      },
      {
        path: 'attendance-records',
        component: AdminAttendanceRecords,
        meta: { title: '考勤记录' }
      },
      {
        path: 'attendance-adjustments',
        component: AdminAttendanceAdjustments,
        meta: { title: '考勤补录' }
      },
      {
        path: 'attendance-exceptions',
        component: AdminAttendanceExceptions,
        meta: { title: '异常处理' }
      },
      {
        path: 'stats',
        component: AdminStats,
        meta: { title: '统计分析' }
      },
      {
        path: 'system-logs',
        component: AdminSystemLogs,
        meta: { title: '操作日志' }
      },
      {
        path: 'system-config',
        component: AdminSystemConfig,
        meta: { title: '系统配置' }
      }
    ]
  }
];
