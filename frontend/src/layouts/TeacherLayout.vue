<template>
  <el-container class="workspace-shell teacher-shell">
    <el-aside width="280px" class="workspace-sidebar teacher-sidebar">
      <div class="brand-block">
        <div class="brand-mark">
          <el-icon><School /></el-icon>
        </div>
        <div>
          <div class="brand-title">智能校园考勤</div>
          <div class="brand-subtitle">教师工作台</div>
        </div>
      </div>

      <div class="sidebar-section">
        <div class="section-title">工作台</div>
        <el-menu :default-active="route.path" class="nav-menu" router>
          <el-menu-item v-for="item in primaryNav" :key="item.path" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="sidebar-section muted">
        <div class="section-title">个人设置</div>
        <el-menu :default-active="route.path" class="nav-menu secondary" router>
          <el-menu-item v-for="item in settingsNav" :key="item.path" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-aside>

    <el-container>
      <el-header class="workspace-header teacher-header">
        <div class="header-left">
          <div class="header-eyebrow">教师端</div>
        </div>
        <el-dropdown trigger="click">
          <span class="user-dropdown">
            <el-avatar size="small" :src="authStore.userInfo?.avatarUrl || undefined" :icon="authStore.userInfo?.avatarUrl ? undefined : UserFilled" />
            <span class="user-name">{{ currentUserText }}</span>
            <el-icon><CaretBottom /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logout">注销登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="workspace-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { School, UserFilled, CaretBottom, Postcard, Lock } from '@element-plus/icons-vue';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const primaryNav = [
  { path: '/teacher/dashboard', label: '工作台', icon: 'House' },
  { path: '/teacher/attendance-report', label: '考勤报表', icon: 'DataAnalysis' },
  { path: '/teacher/leave-notifications', label: '请假通知', icon: 'Bell' }
];

const settingsNav = [
  { path: '/teacher/profile', label: '个人资料', icon: 'Postcard' },
  { path: '/teacher/password', label: '账号安全', icon: 'Lock' }
];

const currentUserText = computed(() => authStore.userInfo?.realName || authStore.userInfo?.username || '教师');

const logout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.workspace-shell {
  min-height: 100vh;
  background: linear-gradient(180deg, #f4f8fc 0%, #edf3fb 100%);
}

.workspace-sidebar {
  border-right: 1px solid rgba(28, 73, 116, 0.08);
  padding: 24px 18px;
  background: linear-gradient(180deg, #123b66 0%, #184976 48%, #1f5b80 100%);
  color: #fff;
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);
}

.brand-mark {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.16);
  font-size: 24px;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
}

.brand-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.72);
}

.sidebar-section {
  margin-top: 8px;
}

.section-title {
  margin-bottom: 10px;
  padding: 0 12px;
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.62);
}

.nav-menu {
  border-right: none;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  margin-bottom: 8px;
  border-radius: 16px;
  color: rgba(255, 255, 255, 0.82);
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

.workspace-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 32px;
  background: transparent;
}

:deep(.el-header) {
  height: auto !important;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-eyebrow {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #6b86a3;
  background: rgba(107, 134, 163, 0.1);
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid rgba(107, 134, 163, 0.2);
}

.user-dropdown {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #d7e4f2;
  box-shadow: 0 10px 24px rgba(28, 76, 129, 0.08);
  cursor: pointer;
}

.user-name {
  font-weight: 600;
  color: #17324d;
}

.workspace-main {
  padding: 0 32px 32px;
}
</style>
