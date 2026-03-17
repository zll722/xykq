<template>
  <div class="admin-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="logo-mark">SC</div>
        <div class="brand-text">
          <strong>校园云</strong>
          <span>考勤系统</span>
        </div>
      </div>

      <nav class="nav-list">
        <RouterLink
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <span class="nav-icon" aria-hidden="true">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>
    </aside>

    <div class="admin-main">
      <header class="topbar">
        <div class="user-text">当前用户：{{ currentUserText }}</div>
        <button data-testid="admin-logout" class="logout-btn" @click="logout">注销</button>
      </header>

      <main class="content-wrap">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const navItems = [
  { path: '/admin/dashboard', label: '首页', icon: '⌂' },
  { path: '/admin/users', label: '用户管理', icon: 'U' },
  { path: '/admin/roles', label: '角色权限', icon: 'R' },
  { path: '/admin/classes', label: '班级管理', icon: 'C' },
  { path: '/admin/courses', label: '课程管理', icon: 'K' },
  { path: '/admin/schedules', label: '排班管理', icon: 'P' },
  { path: '/admin/leave-pending', label: '请假审批', icon: 'A' },
  { path: '/admin/leave-history', label: '审批历史', icon: 'H' },
  { path: '/admin/attendance-records', label: '考勤记录', icon: 'Q' },
  { path: '/admin/attendance-adjustments', label: '考勤补录', icon: 'B' },
  { path: '/admin/attendance-exceptions', label: '异常处理', icon: 'Y' },
  { path: '/admin/stats', label: '统计分析', icon: 'T' },
  { path: '/admin/system-logs', label: '操作日志', icon: 'L' },
  { path: '/admin/system-config', label: '系统配置', icon: 'S' }
];

const currentUserText = computed(() => {
  const name = authStore.userInfo?.realName || '系统管理员';
  const role = authStore.userInfo?.roleCode || 'ADMIN';
  return `${name} (${role})`;
});

const isActive = (path) => route.path === path;

const logout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.admin-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 248px 1fr;
  background: #eff4fa;
}

.sidebar {
  background: linear-gradient(190deg, #172d45 0%, #20384f 45%, #2b3f52 100%);
  color: #d8e4f1;
  padding: 18px 14px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 8px 14px;
  border-bottom: 1px solid rgba(186, 209, 234, 0.2);
}

.logo-mark {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  font-weight: 700;
  font-size: 0.8rem;
  color: #0e355a;
  background: linear-gradient(130deg, #c2e8ff 0%, #9dc8ff 100%);
}

.brand-text {
  display: grid;
  line-height: 1.2;
}

.brand-text strong {
  color: #f1f7ff;
  font-size: 0.97rem;
}

.brand-text span {
  color: #9fbbd4;
  font-size: 0.83rem;
}

.nav-list {
  display: grid;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 11px;
  padding: 9px 10px;
  color: #ccdaea;
  text-decoration: none;
  transition: all 0.2s ease;
}

.nav-item:hover {
  color: #f2f7ff;
  background: rgba(171, 198, 228, 0.15);
}

.nav-item.active {
  color: #ffffff;
  background: linear-gradient(120deg, rgba(63, 124, 205, 0.86), rgba(66, 169, 165, 0.7));
  box-shadow: 0 8px 18px rgba(4, 18, 35, 0.28);
}

.nav-icon {
  width: 20px;
  height: 20px;
  display: grid;
  place-items: center;
  border-radius: 6px;
  border: 1px solid rgba(194, 214, 235, 0.34);
  font-size: 0.72rem;
  font-weight: 700;
}

.admin-main {
  min-width: 0;
  display: grid;
  grid-template-rows: 64px 1fr;
}

.topbar {
  background: #f8fbff;
  border-bottom: 1px solid #d8e3ef;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.user-text {
  color: #244664;
  font-weight: 600;
}

.logout-btn {
  min-height: 36px;
  padding: 0 14px;
}

.content-wrap {
  padding: 18px;
}

@media (max-width: 1080px) {
  .admin-shell {
    grid-template-columns: 208px 1fr;
  }
}

@media (max-width: 860px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    padding-bottom: 10px;
  }

  .nav-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 6px;
  }

  .admin-main {
    grid-template-rows: auto 1fr;
  }

  .topbar {
    min-height: 56px;
    padding: 10px 14px;
  }

  .content-wrap {
    padding: 14px;
  }
}
</style>
