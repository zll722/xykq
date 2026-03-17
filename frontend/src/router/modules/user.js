import UserLayout from '../../layouts/UserLayout.vue';
import UserDashboard from '../../views/user/UserDashboard.vue';
import UserOverview from '../../views/user/UserOverview.vue';
import UserCourses from '../../views/user/UserCourses.vue';
import UserSignIn from '../../views/user/UserSignIn.vue';
import UserAttendanceRecords from '../../views/user/UserAttendanceRecords.vue';
import UserLeaveApply from '../../views/user/UserLeaveApply.vue';
import UserLeaveHistory from '../../views/user/UserLeaveHistory.vue';
import UserLeaveProgress from '../../views/user/UserLeaveProgress.vue';
import UserMessages from '../../views/user/UserMessages.vue';
import UserProfile from '../../views/user/UserProfile.vue';
import UserPassword from '../../views/user/UserPassword.vue';

export const userRoutes = [
  {
    path: '/user',
    component: UserLayout,
    meta: { role: 'USER' },
    children: [
      {
        path: 'dashboard',
        component: UserDashboard,
        meta: { title: '用户首页' }
      },
      {
        path: 'overview',
        component: UserOverview,
        meta: { title: '个人中心' }
      },
      {
        path: 'courses',
        component: UserCourses,
        meta: { title: '我的课程' }
      },
      {
        path: 'sign-in',
        component: UserSignIn,
        meta: { title: '在线签到' }
      },
      {
        path: 'attendance-records',
        component: UserAttendanceRecords,
        meta: { title: '我的考勤' }
      },
      {
        path: 'leave-apply',
        component: UserLeaveApply,
        meta: { title: '请假申请' }
      },
      {
        path: 'leave-history',
        component: UserLeaveHistory,
        meta: { title: '请假记录' }
      },
      {
        path: 'leave-progress',
        component: UserLeaveProgress,
        meta: { title: '审批进度' }
      },
      {
        path: 'messages',
        component: UserMessages,
        meta: { title: '消息通知' }
      },
      {
        path: 'profile',
        component: UserProfile,
        meta: { title: '个人资料' }
      },
      {
        path: 'password',
        component: UserPassword,
        meta: { title: '修改密码' }
      }
    ]
  }
];
