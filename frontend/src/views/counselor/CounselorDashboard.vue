<template>
  <div class="counselor-dashboard">
    <section class="page-hero">
      <div>
        <p class="page-tag">辅导员工作台</p>
        <h1>{{ userName }}，欢迎回来</h1>
        <p>管理您负责班级的学生请假情况，及时处理待审批申请。</p>
      </div>
      <div class="hero-note">
        <strong>{{ stats.pendingLeaveCount ?? 0 }}</strong>
        <span>待审批请假</span>
      </div>
    </section>

    <el-row :gutter="18">
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-inner">
            <el-icon class="stat-icon" style="color:#275f9d"><School /></el-icon>
            <div>
              <div class="stat-value">{{ stats.classCount ?? '-' }}</div>
              <div class="stat-label">负责班级数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-inner">
            <el-icon class="stat-icon" style="color:#3b82f6"><User /></el-icon>
            <div>
              <div class="stat-value">{{ stats.studentCount ?? '-' }}</div>
              <div class="stat-label">管理学生数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-inner">
            <el-icon class="stat-icon" style="color:#f59e0b"><DocumentChecked /></el-icon>
            <div>
              <div class="stat-value">{{ stats.recentHandledCount ?? '-' }}</div>
              <div class="stat-label">近30天审批数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18" style="margin-top: 18px;">
      <el-col :xs="24" :sm="12">
        <el-card class="quick-card" shadow="hover">
          <template #header>
            <span class="card-title">快速入口</span>
          </template>
          <div class="quick-buttons">
            <el-button type="warning" plain @click="$router.push('/counselor/leave-pending')">
              <el-icon><DocumentChecked /></el-icon>
              待审批请假
              <el-badge v-if="stats.pendingLeaveCount > 0" :value="stats.pendingLeaveCount" class="ml-badge" />
            </el-button>
            <el-button type="primary" plain @click="$router.push('/counselor/classes')">
              <el-icon><School /></el-icon>
              我的班级
            </el-button>
            <el-button plain @click="$router.push('/counselor/leave-history')">
              <el-icon><Document /></el-icon>
              审批历史
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import { School, User, DocumentChecked, Document } from '@element-plus/icons-vue';
import { useAuthStore } from '../../stores/auth';
import request from '../../utils/request';

const authStore = useAuthStore();
const userName = computed(() => authStore.userInfo?.realName || '辅导员');
const stats = ref({});

onMounted(async () => {
  try {
    const data = await request.get('/counselor/dashboard');
    stats.value = data || {};
  } catch {
    stats.value = {};
  }
});
</script>

<style scoped>
.counselor-dashboard { display: flex; flex-direction: column; gap: 0; }

.page-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 28px;
  padding: 32px 36px;
  border-radius: 24px;
  background: linear-gradient(135deg, #143a63 0%, #275f9d 100%);
  color: #fff;
}

.page-tag { margin: 0 0 10px; font-size: 12px; letter-spacing: 0.1em; text-transform: uppercase; opacity: 0.72; }
.page-hero h1 { margin: 0 0 10px; font-size: 26px; font-weight: 700; }
.page-hero p { margin: 0; opacity: 0.82; }

.hero-note {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 18px 28px;
  border-radius: 18px;
  background: rgba(255,255,255,0.12);
  white-space: nowrap;
}
.hero-note strong { font-size: 32px; font-weight: 800; }
.hero-note span { font-size: 13px; opacity: 0.8; }

.stat-card { border-radius: 16px; }
.stat-inner { display: flex; align-items: center; gap: 16px; padding: 8px 0; }
.stat-icon { font-size: 32px; }
.stat-value { font-size: 28px; font-weight: 800; color: #15334f; }
.stat-label { font-size: 13px; color: #6b86a3; margin-top: 2px; }

.quick-card { border-radius: 16px; }
.card-title { font-size: 15px; font-weight: 600; color: #15334f; }
.quick-buttons { display: flex; flex-wrap: wrap; gap: 10px; }

.ml-badge {
  margin-left: 6px;
}
</style>
