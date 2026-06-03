import TeacherLayout from '../../layouts/TeacherLayout.vue';
import TeacherDashboard from '../../views/teacher/TeacherDashboard.vue';
import TeacherAttendanceReport from '../../views/teacher/TeacherAttendanceReport.vue';
import TeacherLeaveNotifications from '../../views/teacher/TeacherLeaveNotifications.vue';
import TeacherAttendanceRecords from '../../views/teacher/TeacherAttendanceRecords.vue';
import TeacherAttendanceExceptions from '../../views/teacher/TeacherAttendanceExceptions.vue';
import TeacherAttendanceAdjustments from '../../views/teacher/TeacherAttendanceAdjustments.vue';
import UserProfile from '../../views/user/UserProfile.vue';
import UserPassword from '../../views/user/UserPassword.vue';

export const teacherRoutes = [
  {
    path: '/teacher',
    component: TeacherLayout,
    meta: { role: 'TEACHER' },
    children: [
      { path: '', redirect: '/teacher/dashboard' },
      { path: 'dashboard', component: TeacherDashboard, meta: { title: '教师工作台' } },
      { path: 'leave-notifications', component: TeacherLeaveNotifications, meta: { title: '请假通知' } },
      { path: 'attendance-records', component: TeacherAttendanceRecords, meta: { title: '考勤记录管理' } },
      { path: 'attendance-exceptions', component: TeacherAttendanceExceptions, meta: { title: '异常处理' } },
      { path: 'attendance-adjustments', component: TeacherAttendanceAdjustments, meta: { title: '考勤修正记录' } },
      { path: 'attendance-report', component: TeacherAttendanceReport, meta: { title: '考勤报表' } },
      { path: 'profile', component: UserProfile, meta: { title: '个人资料' } },
      { path: 'password', component: UserPassword, meta: { title: '修改密码' } }
    ]
  }
];
