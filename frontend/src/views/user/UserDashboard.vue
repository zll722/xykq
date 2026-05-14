<template>
  <div class="student-dashboard">
    <section class="hero-panel">
      <div>
        <p class="eyebrow">今日任务</p>
        <h1>你好，{{ displayName }}</h1>
        <p class="hero-text">先看今天有没有课、能不能签到、有没有审批结果，不再让你自己翻菜单找信息。</p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="router.push('/user/sign-in')">去签到</el-button>
        <el-button size="large" @click="router.push('/user/leave-apply')">去请假</el-button>
      </div>
    </section>

    <el-row :gutter="18" class="summary-grid">
      <el-col :xs="24" :sm="12" :xl="6">
        <el-card class="summary-card accent-primary" shadow="hover">
          <span class="summary-label">出勤率</span>
          <strong>{{ overview.presentRate }}%</strong>
          <p>已完成 {{ overview.presentCount }} 次正常出勤</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :xl="6">
        <el-card class="summary-card accent-success" shadow="hover">
          <span class="summary-label">最近审批结果</span>
          <strong>{{ latestApprovalText }}</strong>
          <p>{{ latestApprovalHint }}</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :xl="6">
        <el-card class="summary-card accent-warning" shadow="hover">
          <span class="summary-label">待读通知</span>
          <strong>{{ unreadCount }}</strong>
          <p>{{ unreadCount > 0 ? '建议先查看消息，避免错过审批结果' : '当前没有未读通知' }}</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :xl="6">
        <el-card class="summary-card accent-info" shadow="hover">
          <span class="summary-label">签到状态</span>
          <strong>{{ signStatus.title }}</strong>
          <p>{{ signStatus.description }}</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18" class="content-grid">
      <el-col :xs="24" :xl="15">
        <el-card class="agenda-card" shadow="hover">
          <template #header>
            <div class="section-header">
              <div>
                <span class="section-title">今日课程与待办</span>
                <p>今天最值得关注的事项都放在这里</p>
              </div>
              <el-button text @click="router.push('/user/courses')">查看完整课表</el-button>
            </div>
          </template>

          <div v-if="todaySchedules.length" class="agenda-list">
            <div v-for="item in todaySchedules" :key="item.id" class="agenda-item">
              <div class="agenda-time">
                <strong>{{ item.startTime || '--:--' }}</strong>
                <span>{{ item.weekDayLabel || '今日课程' }}</span>
              </div>
              <div class="agenda-main">
                <h3>{{ item.courseName || '未命名课程' }}</h3>
                <p>{{ item.className || '未分配班级' }} · {{ item.location || '地点待定' }}</p>
              </div>
              <div class="agenda-side">
                <el-tag :type="item.signStatus === 'OPEN' ? 'success' : 'info'">{{ item.signStatusText || '查看详情' }}</el-tag>
              </div>
            </div>
          </div>
          <el-empty v-else description="今天没有课程安排，若你准备请假或查看历史记录，可直接使用右侧快捷任务。" />
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="9">
        <el-card class="task-card" shadow="hover">
          <template #header>
            <div class="section-header compact">
              <div>
                <span class="section-title">快捷任务</span>
                <p>围绕签到、请假、消息快速处理</p>
              </div>
            </div>
          </template>

          <div class="task-stack">
            <button class="task-button primary" @click="router.push('/user/sign-in')">
              <span>在线签到</span>
              <small>查看当前是否可以签到</small>
            </button>
            <button class="task-button" @click="router.push('/user/leave-apply')">
              <span>提交请假</span>
              <small>按时间、原因和材料一步完成申请</small>
            </button>
            <button class="task-button" @click="router.push('/user/messages')">
              <span>查看消息</span>
              <small>集中查看审批结果和系统通知</small>
            </button>
            <button class="task-button" @click="router.push('/user/attendance-records')">
              <span>查看考勤</span>
              <small>快速定位异常、迟到和缺勤记录</small>
            </button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { myOverviewApi, listMySchedulesApi } from '../../api/attendance';
import { listMyLeaveProgressApi } from '../../api/leave';
import { listMessagesApi } from '../../api/message';

const router = useRouter();
const authStore = useAuthStore();

const overview = ref({
  totalCount: 0,
  presentCount: 0,
  lateCount: 0,
  absentCount: 0,
  presentRate: 0
});
const todaySchedules = ref([]);
const latestApproval = ref(null);
const unreadCount = ref(0);

const displayName = computed(() => authStore.userInfo?.realName || authStore.userInfo?.username || '同学');

const latestApprovalText = computed(() => {
  if (!latestApproval.value) return '暂无审批';
  return latestApproval.value.approvalStatusText || latestApproval.value.statusText || '有新进展';
});

const latestApprovalHint = computed(() => {
  if (!latestApproval.value) return '你最近没有新的请假审批动态';
  return latestApproval.value.reason || latestApproval.value.leaveType || '可进入请假中心查看详情';
});

const signStatus = computed(() => {
  if (!todaySchedules.value.length) {
    return {
      title: '今日无待签课程',
      description: '如果你认为应该能签到，先检查课程安排和签到开放时间'
    };
  }
  const openCourse = todaySchedules.value.find((item) => item.signStatus === 'OPEN');
  if (openCourse) {
    return {
      title: '可立即签到',
      description: `${openCourse.courseName || '当前课程'} 已开放签到`
    };
  }
  return {
    title: '暂未开放',
    description: '今天有课程，但当前时间段暂不可签到'
  };
});

const loadOverview = async () => {
  try {
    overview.value = await myOverviewApi();
  } catch (error) {
    console.error(error);
  }
};

const loadSchedules = async () => {
  try {
    const data = await listMySchedulesApi();
    todaySchedules.value = Array.isArray(data) ? data.slice(0, 4) : [];
  } catch (error) {
    console.error(error);
  }
};

const loadLeaveProgress = async () => {
  try {
    const data = await listMyLeaveProgressApi();
    latestApproval.value = Array.isArray(data) && data.length ? data[0] : null;
  } catch (error) {
    latestApproval.value = null;
    console.error(error);
  }
};

const loadMessages = async () => {
  try {
    const data = await listMessagesApi();
    const list = Array.isArray(data) ? data : [];
    unreadCount.value = list.filter((item) => item.readFlag !== 1).length;
  } catch (error) {
    unreadCount.value = 0;
    console.error(error);
  }
};

onMounted(() => {
  loadOverview();
  loadSchedules();
  loadLeaveProgress();
  loadMessages();
});
</script>

<style scoped>
.student-dashboard {
  display: grid;
  gap: 18px;
}

.hero-panel {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #123b66 0%, #2b66b7 58%, #5eb0b0 100%);
  color: #fff;
  box-shadow: 0 24px 48px rgba(31, 78, 130, 0.18);
}

.eyebrow {
  margin: 0 0 10px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.hero-panel h1 {
  margin: 0;
  color: #fff;
  font-size: 30px;
}

.hero-text {
  max-width: 560px;
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.hero-actions {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.summary-grid,
.content-grid {
  margin: 0;
}

.summary-card {
  border: none;
  border-radius: 24px;
}

.summary-card :deep(.el-card__body) {
  display: grid;
  gap: 10px;
}

.summary-label {
  color: #607894;
  font-size: 13px;
}

.summary-card strong {
  font-size: 28px;
  color: #15334f;
}

.summary-card p {
  margin: 0;
  color: #6d839a;
}

.accent-primary { box-shadow: inset 0 4px 0 #2e6ddf; }
.accent-success { box-shadow: inset 0 4px 0 #2ca877; }
.accent-warning { box-shadow: inset 0 4px 0 #d48819; }
.accent-info { box-shadow: inset 0 4px 0 #2e86de; }

.agenda-card,
.task-card {
  border-radius: 24px;
  border: none;
}

.section-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.section-header p {
  margin: 6px 0 0;
  color: #7890a8;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #17324d;
}

.agenda-list {
  display: grid;
  gap: 12px;
}

.agenda-item {
  display: grid;
  grid-template-columns: 120px 1fr auto;
  gap: 16px;
  padding: 16px 18px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f8fbff 0%, #eef5fb 100%);
}

.agenda-time strong,
.agenda-main h3 {
  display: block;
}

.agenda-time strong {
  font-size: 22px;
  color: #143551;
}

.agenda-time span,
.agenda-main p {
  color: #7188a1;
}

.agenda-main h3 {
  margin: 0 0 6px;
}

.agenda-main p {
  margin: 0;
}

.task-stack {
  display: grid;
  gap: 12px;
}

.task-button {
  text-align: left;
  padding: 18px 20px;
  border-radius: 20px;
  border: 1px solid #d6e3f0;
  background: #fff;
  color: #17324d;
  cursor: pointer;
  box-shadow: 0 14px 26px rgba(31, 77, 126, 0.08);
}

.task-button span {
  display: block;
  font-size: 16px;
  font-weight: 700;
}

.task-button small {
  display: block;
  margin-top: 6px;
  color: #7590ab;
}

.task-button.primary {
  border-color: rgba(46, 109, 223, 0.22);
  background: linear-gradient(135deg, #edf4ff 0%, #f4fbff 100%);
}
</style>
