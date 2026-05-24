<template>
  <div class="teacher-dashboard">
    <section class="page-hero">
      <div>
        <p class="page-tag">教师工作台</p>
        <h1>{{ userInfo?.realName || '教师' }}，欢迎回来</h1>
        <p>查看您授课班级的请假情况，及时了解课堂出勤动态。</p>
      </div>
      <div class="hero-note">
        <strong>今日请假</strong>
        <span>{{ stats.todayLeaveCount ?? 0 }} 人</span>
      </div>
    </section>

    <el-row :gutter="18">
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-inner">
            <el-icon class="stat-icon course-icon"><Reading /></el-icon>
            <div>
              <div class="stat-value">{{ stats.courseCount ?? '-' }}</div>
              <div class="stat-label">授课课程数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-inner">
            <el-icon class="stat-icon class-icon"><CollectionTag /></el-icon>
            <div>
              <div class="stat-value">{{ stats.classCount ?? '-' }}</div>
              <div class="stat-label">涉及班级数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-inner">
            <el-icon class="stat-icon leave-icon"><DocumentChecked /></el-icon>
            <div>
              <div class="stat-value">{{ stats.recentApprovedLeaves ?? '-' }}</div>
              <div class="stat-label">近30天请假人次</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="quick-card" shadow="hover" style="margin-top: 18px;">
      <template #header>
        <div class="card-header">
          <span class="card-title">快速入口</span>
        </div>
      </template>
      <el-button type="primary" @click="$router.push('/teacher/leave-notifications')">
        <el-icon><Bell /></el-icon>
        查看请假通知
      </el-button>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Reading, CollectionTag, DocumentChecked, Bell } from '@element-plus/icons-vue';
import { useAuthStore } from '../../stores/auth';
import request from '../../utils/request';

const authStore = useAuthStore();
const userInfo = authStore.userInfo;
const stats = ref({});

const loadStats = async () => {
  try {
    const data = await request.get('/teacher/dashboard');
    stats.value = data || {};
  } catch {
    stats.value = {};
  }
};

onMounted(loadStats);
</script>

<style scoped>
.teacher-dashboard {
  display: flex;
  flex-direction: column;
  gap: 0;
}

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

.page-tag {
  margin: 0 0 10px;
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  opacity: 0.72;
}

.page-hero h1 {
  margin: 0 0 10px;
  font-size: 28px;
  font-weight: 700;
}

.page-hero p {
  margin: 0;
  opacity: 0.82;
}

.hero-note {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 18px 28px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.12);
  white-space: nowrap;
}

.hero-note strong {
  font-size: 28px;
  font-weight: 800;
}

.hero-note span {
  font-size: 13px;
  opacity: 0.8;
}

.stat-card {
  border-radius: 18px;
  margin-bottom: 18px;
}

.stat-inner {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 8px 4px;
}

.stat-icon {
  font-size: 36px;
  padding: 14px;
  border-radius: 16px;
}

.course-icon {
  background: rgba(20, 58, 99, 0.1);
  color: #143a63;
}

.class-icon {
  background: rgba(18, 59, 102, 0.1);
  color: #123b66;
}

.leave-icon {
  background: rgba(215, 95, 30, 0.1);
  color: #d75f1e;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  color: #15334f;
}

.stat-label {
  font-size: 13px;
  color: #6b86a3;
  margin-top: 4px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #15334f;
}
</style>
