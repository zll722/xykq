<template>
  <el-container class="workspace-shell user-shell">
    <el-aside width="280px" class="workspace-sidebar user-sidebar">
      <div class="brand-block">
        <div class="brand-mark">
          <el-icon><School /></el-icon>
        </div>
        <div>
          <div class="brand-title">智能校园考勤</div>
          <div class="brand-subtitle">学生任务中心</div>
        </div>
      </div>

      <div class="sidebar-section">
        <div class="section-title">高频任务</div>
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
          <el-menu-item v-for="item in secondaryNav" :key="item.path" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-aside>

    <el-container>
      <el-header class="workspace-header user-header">
        <div class="header-left">
          <div class="header-eyebrow">学生端</div>
        </div>
        <div class="header-actions">
          <el-dropdown trigger="click">
            <span class="user-dropdown">
              <el-avatar size="small" :icon="UserFilled" />
              <span class="user-name">{{ currentUserText }}</span>
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">注销登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
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
import { School, UserFilled, CaretBottom } from '@element-plus/icons-vue';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const primaryNav = [
  { path: '/user/dashboard', label: '工作台', icon: 'House', description: '查看今日待办、签到状态与审批提醒' },
  { path: '/user/courses', label: '课程与课表', icon: 'Reading', description: '查看课程安排与上课信息' },
  { path: '/user/sign-in', label: '签到中心', icon: 'Location', description: '完成在线签到并了解当前是否可签到' },
  { path: '/user/attendance-records', label: '考勤记录', icon: 'List', description: '查看历史出勤、迟到、缺勤与请假记录' },
  { path: '/user/leave-apply', label: '请假中心', icon: 'DocumentAdd', description: '提交请假、查看进度与历史审批结果' },
  { path: '/user/messages', label: '消息通知', icon: 'Bell', description: '集中查看审批、签到和系统通知' }
];

const secondaryNav = [
  { path: '/user/profile', label: '个人资料', icon: 'Postcard', description: '维护姓名、联系方式与头像信息' },
  { path: '/user/password', label: '账号安全', icon: 'Lock', description: '修改密码并维护账号安全' }
];

const navMap = computed(() => [...primaryNav, ...secondaryNav]);
const currentNav = computed(() => navMap.value.find((item) => item.path === route.path));

const currentTitle = computed(() => currentNav.value?.label || '学生工作台');
const currentDescription = computed(() => currentNav.value?.description || '围绕课程、签到、请假和通知完成日常任务');

const currentUserText = computed(() => authStore.userInfo?.realName || authStore.userInfo?.username || '学生用户');

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

.sidebar-section + .sidebar-section {
  margin-top: 22px;
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
