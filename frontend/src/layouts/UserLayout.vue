<template>
  <div class="user-shell">
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

    <div class="user-main">
      <header class="topbar">
        <div class="user-text">当前用户：{{ currentUserText }}</div>
        <button data-testid="user-logout" class="logout-btn" @click="logout">注销</button>
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
  { path: '/user/dashboard', label: '首页', icon: '⌂' },
  { path: '/user/overview', label: '个人中心', icon: 'O' },
  { path: '/user/courses', label: '我的课程', icon: 'K' },
  { path: '/user/sign-in', label: '在线签到', icon: 'Q' },
  { path: '/user/attendance-records', label: '我的考勤', icon: 'A' },
  { path: '/user/leave-apply', label: '请假申请', icon: 'J' },
  { path: '/user/leave-progress', label: '审批进度', icon: 'P' },
  { path: '/user/leave-history', label: '请假记录', icon: 'H' },
  { path: '/user/messages', label: '消息通知', icon: 'M' },
  { path: '/user/profile', label: '个人资料', icon: 'D' },
  { path: '/user/password', label: '修改密码', icon: 'S' }
];

const currentUserText = computed(() => {
  const name = authStore.userInfo?.realName || '学生用户';
  const role = authStore.userInfo?.roleCode || 'USER';
  return `${name} (${role})`;
});

const isActive = (path) => route.path === path;

const logout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.user-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 248px 1fr;
  background: #eff4fa;
}

.sidebar {
  background: linear-gradient(190deg, #15334f 0%, #1f4362 50%, #25536d 100%);
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
  color: #0f3a5b;
  background: linear-gradient(130deg, #bce7ff 0%, #95d3ff 100%);
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
  color: #a8c7df;
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
  background: linear-gradient(120deg, rgba(53, 141, 197, 0.9), rgba(54, 179, 152, 0.72));
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

.user-main {
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
  .user-shell {
    grid-template-columns: 208px 1fr;
  }
}

@media (max-width: 860px) {
  .user-shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    padding-bottom: 10px;
  }

  .nav-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 6px;
  }

  .user-main {
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
