import { createRouter, createWebHistory } from 'vue-router';
import { adminRoutes } from './modules/admin';
import { userRoutes } from './modules/user';
import { useAuthStore } from '../stores/auth';

const LoginPage = () => import('../views/common/LoginPage.vue');
const ForbiddenPage = () => import('../views/common/ForbiddenPage.vue');

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  { path: '/403', component: ForbiddenPage },
  ...adminRoutes,
  ...userRoutes
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  if (to.path === '/login') {
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
  if (routeRole && authStore.userInfo.roleCode !== routeRole) {
    next('/403');
    return;
  }
  next();
});

export default router;
