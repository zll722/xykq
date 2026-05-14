<template>
  <el-container class="workspace-shell admin-shell">
    <el-aside width="300px" class="workspace-sidebar admin-sidebar">
      <div class="brand-block">
        <div class="brand-mark">
          <el-icon><DataAnalysis /></el-icon>
        </div>
        <div>
          <div class="brand-title">校园考勤管理台</div>
          <div class="brand-subtitle">以任务驱动的管理控制台</div>
        </div>
      </div>

      <div v-for="group in navGroups" :key="group.label" class="sidebar-section">
        <div class="section-title">{{ group.label }}</div>
        <el-menu :default-active="route.path" class="nav-menu" router>
          <el-menu-item v-for="item in group.items" :key="item.path" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-aside>

    <el-container>
      <el-header class="workspace-header admin-header">
        <div class="header-left">
          <div class="header-eyebrow">管理端</div>
        </div>
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
import { DataAnalysis, UserFilled, CaretBottom } from '@element-plus/icons-vue';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const navGroups = [
  {
    label: '工作台',
    items: [
      { path: '/admin/dashboard', label: '总览中心', icon: 'House', description: '查看待审批、异常考勤和核心统计' }
    ]
  },
  {
    label: '教学基础数据',
    items: [
      { path: '/admin/users', label: '用户管理', icon: 'User', description: '管理学生和管理员账号状态' },
      { path: '/admin/classes', label: '班级管理', icon: 'CollectionTag', description: '维护班级基础信息和容量' },
      { path: '/admin/courses', label: '课程管理', icon: 'Reading', description: '维护课程、教师和上课地点' },
      { path: '/admin/schedules', label: '排班管理', icon: 'Calendar', description: '按课程与班级维护排班信息' }
    ]
  },
  {
    label: '考勤与请假',
    items: [
      { path: '/admin/leave-pending', label: '待审批请假', icon: 'DocumentChecked', description: '集中处理待审批请假单' },
      { path: '/admin/leave-history', label: '审批历史', icon: 'DocumentCopy', description: '回溯历史审批结果和处理记录' },
      { path: '/admin/attendance-records', label: '考勤记录', icon: 'List', description: '查看和筛选原始考勤结果' },
      { path: '/admin/attendance-adjustments', label: '考勤修正', icon: 'EditPen', description: '对考勤记录进行补录与修正' },
      { path: '/admin/attendance-exceptions', label: '异常处理', icon: 'Warning', description: '查看和处理异常考勤' }
    ]
  },
  {
    label: '统计与系统',
    items: [
      { path: '/admin/stats', label: '统计分析', icon: 'PieChart', description: '按课程、班级和时间查看统计并导出' },
      { path: '/admin/system-logs', label: '操作日志', icon: 'Tickets', description: '追踪关键操作和系统调用记录' }
    ]
  }
];

const flatNav = computed(() => navGroups.flatMap((group) => group.items));
const currentNav = computed(() => flatNav.value.find((item) => item.path === route.path));
const currentTitle = computed(() => currentNav.value?.label || '管理控制台');
const currentDescription = computed(() => currentNav.value?.description || '围绕审批、考勤和系统配置处理校园日常事务');
const currentUserText = computed(() => authStore.userInfo?.realName || authStore.userInfo?.username || '管理员');

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
  background: linear-gradient(180deg, #112d49 0%, #173a5c 50%, #1f4b69 100%);
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
  margin-top: 18px;
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
