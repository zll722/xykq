<template>
  <el-container class="layout-root">
    <el-aside width="240px" class="sidebar">
      <div class="brand">
        <div class="brand-icon">
          <el-icon :size="28"><UserFilled /></el-icon>
        </div>
        <div class="brand-text">
          <div class="brand-name">智能校园考勤</div>
          <div class="brand-sub">辅导员工作台</div>
        </div>
      </div>

      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="rgba(255,255,255,0.75)"
        active-text-color="#ffffff"
        class="side-menu"
      >
        <div class="menu-group-label">工作台</div>
        <el-menu-item index="/counselor/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>总览中心</span>
        </el-menu-item>

        <div class="menu-group-label">班级管理</div>
        <el-menu-item index="/counselor/classes">
          <el-icon><School /></el-icon>
          <span>我的班级</span>
        </el-menu-item>

        <div class="menu-group-label">请假管理</div>
        <el-menu-item index="/counselor/leave-pending">
          <el-icon><DocumentChecked /></el-icon>
          <span>待审批请假</span>
        </el-menu-item>
        <el-menu-item index="/counselor/leave-history">
          <el-icon><Document /></el-icon>
          <span>审批历史</span>
        </el-menu-item>

        <div class="menu-group-label">个人设置</div>
        <el-menu-item index="/counselor/profile">
          <el-icon><Postcard /></el-icon>
          <span>个人资料</span>
        </el-menu-item>
        <el-menu-item index="/counselor/password">
          <el-icon><Lock /></el-icon>
          <span>账号安全</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="32" :src="authStore.userInfo?.avatarUrl || undefined" style="background:#2a5fa8; flex-shrink:0">
              {{ userInitial }}
            </el-avatar>
            <span class="user-name">{{ userName }}</span>
            <el-icon class="arrow"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">注销登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-aside>

    <el-container class="main-wrap">
      <el-header class="workspace-header">
        <el-tag type="primary" effect="dark" size="small" class="role-badge">辅导员端</el-tag>
        <div class="header-right">
          <span class="header-user">{{ userName }}</span>
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
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import {
  UserFilled, HomeFilled, DocumentChecked, Document, ArrowDown, School, Postcard, Lock
} from '@element-plus/icons-vue';

const router = useRouter();
const authStore = useAuthStore();
const userName = computed(() => authStore.userInfo?.realName || '辅导员');
const userInitial = computed(() => (userName.value || '辅').charAt(0));

function handleCommand(cmd) {
  if (cmd === 'logout') {
    authStore.logout();
    router.push('/login?logout=1');
  }
}
</script>

<style scoped>
.layout-root {
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background: linear-gradient(180deg, #123b66 0%, #184976 48%, #1f5b80 100%);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 20px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.brand-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255,255,255,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.brand-name {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
}

.brand-sub {
  font-size: 11px;
  color: rgba(255,255,255,0.55);
  margin-top: 2px;
}

.side-menu {
  flex: 1;
  overflow-y: auto;
  border: none !important;
  padding: 8px 10px;
}

.side-menu :deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 2px;
  height: 42px;
  font-size: 14px;
}

.side-menu :deep(.el-menu-item.is-active) {
  background: rgba(255,255,255,0.18) !important;
  color: #fff !important;
}

.side-menu :deep(.el-menu-item:hover) {
  background: rgba(255,255,255,0.1) !important;
  color: #fff !important;
}

.menu-group-label {
  font-size: 11px;
  color: rgba(255,255,255,0.4);
  letter-spacing: 0.06em;
  text-transform: uppercase;
  padding: 14px 10px 4px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 8px;
  border-radius: 10px;
  transition: background 0.2s;
}

.user-info:hover {
  background: rgba(255,255,255,0.1);
}

.user-name {
  font-size: 14px;
  color: rgba(255,255,255,0.85);
  flex: 1;
}

.arrow {
  color: rgba(255,255,255,0.4);
  font-size: 12px;
}

.workspace-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 28px;
  background: #fff;
  border-bottom: 1px solid #eef0f3;
  height: 56px !important;
}

.role-badge {
  font-size: 12px;
  border-radius: 8px;
}

.header-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-user {
  font-size: 14px;
  color: #5f7893;
}

.workspace-main {
  background: #f4f6f9;
  overflow-y: auto;
  padding: 28px;
}
</style>
