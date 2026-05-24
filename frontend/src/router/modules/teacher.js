import TeacherLayout from '../../layouts/TeacherLayout.vue';
import TeacherDashboard from '../../views/teacher/TeacherDashboard.vue';
import TeacherLeaveNotifications from '../../views/teacher/TeacherLeaveNotifications.vue';
import TeacherAttendanceReport from '../../views/teacher/TeacherAttendanceReport.vue';
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
      { path: 'attendance-report', component: TeacherAttendanceReport, meta: { title: '考勤报表' } },
      { path: 'profile', component: UserProfile, meta: { title: '个人资料' } },
      { path: 'password', component: UserPassword, meta: { title: '修改密码' } }
    ]
  }
];
