<template>
  <div class="sign-page">
    <section class="sign-hero">
      <div>
        <p class="hero-tag">签到中心</p>
        <h1>只保留你现在最需要的签到信息</h1>
        <p>先看当前有哪些课可以签到、每门课的时间地点和最新签到结果，避免操作后提示难看或信息不好找。</p>
      </div>
      <div class="hero-actions">
        <el-button data-testid="sign-in-refresh" type="primary" plain @click="loadSchedules" :loading="loading">
          刷新课程
        </el-button>
      </div>
    </section>

    <el-alert
      v-if="result"
      :title="resultTitle"
      :description="resultDescription"
      :type="resultType"
      show-icon
      closable
      class="feedback-alert"
      @close="result = null"
    />
    <el-alert
      v-if="error"
      :title="error"
      type="error"
      show-icon
      closable
      class="feedback-alert"
      @close="error = ''"
    />

    <el-card class="schedule-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">待签到课程</span>
            <p>系统会按照当前课程安排展示可参与签到的课程。</p>
          </div>
          <div class="card-meta">共 {{ schedules.length }} 门</div>
        </div>
      </template>

      <div v-loading="loading" class="schedule-list">
        <el-empty v-if="!loading && schedules.length === 0" description="暂无需要签到的课程" />

        <el-row v-else :gutter="20">
          <el-col v-for="item in schedules" :key="item.scheduleId" :xs="24" :sm="12" :xl="8" class="schedule-col">
            <article class="course-item">
              <div class="course-top">
                <div>
                  <h3>{{ item.courseName || '未命名课程' }}</h3>
                  <p>周{{ item.weekDay }} · {{ item.className || '班级待确认' }}</p>
                </div>
                <el-tag size="small" effect="dark" type="info">待签到</el-tag>
              </div>

              <div class="course-grid">
                <div class="info-panel">
                  <span>上课时间</span>
                  <strong>{{ item.startTime || '--:--' }} - {{ item.endTime || '--:--' }}</strong>
                </div>
                <div class="info-panel">
                  <span>上课地点</span>
                  <strong>{{ item.location || '默认教室' }}</strong>
                </div>
              </div>

              <el-button
                type="primary"
                size="large"
                class="submit-button"
                data-testid="sign-in-submit"
                :loading="submittingId === item.scheduleId"
                @click="submitSignIn(item.scheduleId)"
              >
                立即签到
              </el-button>
            </article>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { listMySchedulesApi, signInApi } from '../../api/attendance';

const schedules = ref([]);
const result = ref(null);
const error = ref('');
const loading = ref(false);
const submittingId = ref(null);

const statusLabelMap = {
  PRESENT: '签到成功',
  LATE: '签到成功，已记为迟到',
  ABSENT: '签到已提交，当前状态为缺勤'
};

const resultType = computed(() => {
  if (!result.value) return 'success';
  if (result.value.status === 'ABSENT') return 'warning';
  return 'success';
});

const resultTitle = computed(() => {
  if (!result.value) return '';
  return statusLabelMap[result.value.status] || `签到结果：${result.value.status || '已完成'}`;
});

const resultDescription = computed(() => {
  if (!result.value) return '';
  const signedAtText = result.value.signedAt || '时间待确认';
  return `签到时间：${signedAtText}`;
});

const loadSchedules = async () => {
  loading.value = true;
  try {
    const data = await listMySchedulesApi();
    schedules.value = Array.isArray(data) ? data : [];
  } catch (e) {
    schedules.value = [];
    ElMessage.error('加载排班失败');
  } finally {
    loading.value = false;
  }
};

const submitSignIn = async (scheduleId) => {
  result.value = null;
  error.value = '';
  submittingId.value = scheduleId;
  try {
    const res = await signInApi({ scheduleId: Number(scheduleId) });
    result.value = res;
    ElMessage.success(statusLabelMap[res?.status] || '签到成功');
    await loadSchedules();
  } catch (e) {
    error.value = e?.message || '签到失败';
    ElMessage.error(error.value);
  } finally {
    submittingId.value = null;
  }
};

onMounted(loadSchedules);
</script>

<style scoped>
.sign-page {
  display: grid;
  gap: 18px;
}

.sign-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #14375b 0%, #2d67b6 56%, #62aeb1 100%);
  color: #fff;
  box-shadow: 0 24px 48px rgba(31, 78, 130, 0.18);
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.sign-hero h1 {
  margin: 0;
  color: #fff;
}

.sign-hero p {
  margin: 10px 0 0;
  max-width: 680px;
  color: rgba(255, 255, 255, 0.84);
}

.hero-actions {
  display: flex;
  align-items: flex-start;
}

.feedback-alert {
  border-radius: 20px;
}

.schedule-card {
  border: none;
  border-radius: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #17324d;
}

.card-header p {
  margin: 6px 0 0;
  color: #7890a8;
}

.card-meta {
  align-self: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: #f3f8ff;
  color: #56738f;
  font-size: 13px;
}

.schedule-list {
  min-height: 160px;
}

.schedule-col {
  margin-bottom: 20px;
}

.course-item {
  height: 100%;
  display: grid;
  gap: 16px;
  padding: 22px;
  border: 1px solid #dbe7f2;
  border-radius: 24px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: 0 14px 30px rgba(33, 77, 126, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.course-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 36px rgba(33, 77, 126, 0.12);
}

.course-top {
  display: flex;
  justify-content: space-between;
  gap: 14px;
}

.course-top h3 {
  margin: 0;
  color: #17324d;
}

.course-top p {
  margin: 8px 0 0;
  color: #6f88a2;
}

.course-grid {
  display: grid;
  gap: 12px;
}

.info-panel {
  padding: 14px 16px;
  border-radius: 18px;
  background: #f3f8ff;
}

.info-panel span,
.info-panel strong {
  display: block;
}

.info-panel span {
  color: #7890a8;
  font-size: 13px;
}

.info-panel strong {
  margin-top: 6px;
  color: #17324d;
}

.submit-button {
  width: 100%;
}

@media (max-width: 900px) {
  .sign-hero,
  .card-header,
  .course-top {
    flex-direction: column;
  }
}
</style>
