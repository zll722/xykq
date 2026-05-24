import { createRouter, createWebHistory } from 'vue-router';
import { adminRoutes } from './modules/admin';
import { userRoutes } from './modules/user';
import { teacherRoutes } from './modules/teacher';
import counselorRoutes from './modules/counselor';
import { useAuthStore } from '../stores/auth';

const LoginPage = () => import('../views/common/LoginPage.vue');
const RegisterPage = () => import('../views/common/RegisterPage.vue');
const ForbiddenPage = () => import('../views/common/ForbiddenPage.vue');

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  { path: '/403', component: ForbiddenPage },
  ...adminRoutes,
  ...teacherRoutes,
  ...counselorRoutes,
  ...userRoutes
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  if (to.path === '/login' || to.path === '/register') {
    next();
    return;
  }
  if (!authStore.token) {
    next('/login');
    return;
  }
  if (!authStore.userInfo) {
    try {
      await authStore.fetchMe();
    } catch (e) {
      authStore.logout();
      next('/login');
      return;
    }
  }
  const routeRole = to.meta?.role;
  if (routeRole) {
    const userRole = authStore.userInfo.roleCode;
    const allowed = userRole === routeRole || userRole === 'ADMIN';
    if (!allowed) {
      next('/403');
      return;
    }
  }
  next();
});

export default router;
