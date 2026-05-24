import CounselorLayout from '../../layouts/CounselorLayout.vue';
import CounselorDashboard from '../../views/counselor/CounselorDashboard.vue';
import CounselorLeavePending from '../../views/counselor/CounselorLeavePending.vue';
import CounselorLeaveHistory from '../../views/counselor/CounselorLeaveHistory.vue';
import CounselorClasses from '../../views/counselor/CounselorClasses.vue';
import UserProfile from '../../views/user/UserProfile.vue';
import UserPassword from '../../views/user/UserPassword.vue';

export default [
  {
    path: '/counselor',
    component: CounselorLayout,
    meta: { role: 'COUNSELOR' },
    children: [
      { path: '', redirect: '/counselor/dashboard' },
      { path: 'dashboard', component: CounselorDashboard, meta: { role: 'COUNSELOR' } },
      { path: 'leave-pending', component: CounselorLeavePending, meta: { role: 'COUNSELOR' } },
      { path: 'leave-history', component: CounselorLeaveHistory, meta: { role: 'COUNSELOR' } },
      { path: 'classes', component: CounselorClasses, meta: { role: 'COUNSELOR' } },
      { path: 'profile', component: UserProfile, meta: { role: 'COUNSELOR' } },
      { path: 'password', component: UserPassword, meta: { role: 'COUNSELOR' } },
    ]
  }
];
