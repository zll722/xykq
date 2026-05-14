<template>
  <div class="course-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">我的课程</p>
        <h1>按课程内容看安排，不再先看排班编号</h1>
        <p>把课程名、上课时间和地点放在主视图，避免班级 ID、排班 ID 抢走注意力。</p>
      </div>
      <div class="hero-side">
        <strong>{{ list.length }}</strong>
        <span>门课程安排</span>
      </div>
    </section>

    <el-card class="course-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">本周课程安排</span>
            <p>优先关注你今天和最近的课程，方便直接去签到或准备请假。</p>
          </div>
          <el-button @click="loadData" :loading="loading">刷新</el-button>
        </div>
      </template>

      <div v-if="list.length" class="course-list" v-loading="loading">
        <article v-for="item in list" :key="item.scheduleId" class="course-item">
          <div class="course-top">
            <div>
              <h3>{{ item.courseName || '未命名课程' }}</h3>
              <p>{{ weekdayLabel(item.weekDay) }} {{ item.startTime || '--:--' }} - {{ item.endTime || '--:--' }}</p>
            </div>
            <el-tag type="info">{{ item.className || '班级待确认' }}</el-tag>
          </div>

          <div class="course-grid">
            <div class="info-block">
              <span>上课地点</span>
              <strong>{{ item.location || '地点待定' }}</strong>
            </div>
            <div class="info-block">
              <span>课程标识</span>
              <strong>{{ item.courseCode || `课程#${item.courseId || '-'}` }}</strong>
            </div>
            <div class="info-block">
              <span>排班信息</span>
              <strong>{{ item.scheduleId ? `排班 ${item.scheduleId}` : '排班待生成' }}</strong>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-else :description="loading ? '正在加载课程安排' : '当前没有课程安排，若你认为数据不完整，可稍后刷新或联系管理员。'" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { listMySchedulesApi } from '../../api/attendance';

const list = ref([]);
const loading = ref(false);

const weekdayLabel = (weekDay) => {
  const map = {
    1: '周一',
    2: '周二',
    3: '周三',
    4: '周四',
    5: '周五',
    6: '周六',
    7: '周日'
  };
  return map[weekDay] || `周${weekDay || '-'}`;
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await listMySchedulesApi();
    list.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error(error);
    ElMessage.error('课程加载失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

onMounted(loadData);
</script>

<style scoped>
.course-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #173860 0%, #2f6ca7 55%, #57aeb0 100%);
  color: #fff;
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.page-hero h1 {
  margin: 0;
  color: #fff;
}

.page-hero p {
  margin: 10px 0 0;
  max-width: 660px;
  color: rgba(255, 255, 255, 0.84);
}

.hero-side {
  min-width: 160px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.12);
}

.hero-side strong,
.hero-side span {
  display: block;
}

.hero-side strong {
  font-size: 32px;
}

.course-card {
  border-radius: 24px;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.card-header p {
  margin: 6px 0 0;
  color: #7890a8;
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: #17324d;
}

.course-list {
  display: grid;
  gap: 14px;
}

.course-item {
  border: 1px solid #dbe7f2;
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  padding: 20px;
}

.course-top {
  display: flex;
  justify-content: space-between;
  gap: 14px;
}

.course-top h3 {
  margin: 0 0 8px;
}

.course-top p {
  margin: 0;
  color: #6e86a0;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.info-block {
  padding: 14px 16px;
  border-radius: 18px;
  background: #f3f8ff;
}

.info-block span,
.info-block strong {
  display: block;
}

.info-block span {
  color: #7890a8;
  font-size: 13px;
}

.info-block strong {
  margin-top: 6px;
  color: #17324d;
}

@media (max-width: 900px) {
  .page-hero,
  .course-top,
  .card-header {
    flex-direction: column;
  }

  .course-grid {
    grid-template-columns: 1fr;
  }
}
</style>
