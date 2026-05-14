<template>
  <div class="attendance-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">我的考勤</p>
        <h1>重点看课程、日期和结果，不再先看记录编号</h1>
        <p>把你最关心的签到结果、时间和异常状态放到前面，方便快速判断是否需要补签或说明情况。</p>
      </div>
      <div class="hero-side">
        <strong>{{ list.length }}</strong>
        <span>条考勤记录</span>
      </div>
    </section>

    <el-card class="attendance-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">近期考勤记录</span>
            <p>优先关注迟到、缺勤和请假记录，避免遗漏需要处理的异常。</p>
          </div>
          <el-button @click="loadData" :loading="loading">刷新</el-button>
        </div>
      </template>

      <div v-if="list.length" class="record-list" v-loading="loading">
        <article v-for="item in list" :key="item.id" class="record-item">
          <div class="record-top">
            <div>
              <h3>{{ item.courseName || '未命名课程' }}</h3>
              <p>{{ item.attendanceDate || '日期待确认' }}</p>
            </div>
            <el-tag :type="getStatusType(item.status)">{{ getStatusLabel(item.status) }}</el-tag>
          </div>

          <div class="record-grid">
            <div class="info-block">
              <span>签到时间</span>
              <strong>{{ item.signedAt || '未签到' }}</strong>
            </div>
            <div class="info-block">
              <span>记录标识</span>
              <strong>{{ item.id ? `记录 ${item.id}` : '待生成' }}</strong>
            </div>
            <div class="info-block wide">
              <span>结果说明</span>
              <strong>{{ getStatusHint(item.status) }}</strong>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-else :description="loading ? '正在加载考勤记录' : '当前没有考勤记录，去签到中心完成课程签到后会在这里显示。'">
        <el-button type="primary" @click="router.push('/user/sign-in')">去签到中心</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { listMyAttendanceRecordsApi } from '../../api/attendance';

const router = useRouter();
const list = ref([]);
const loading = ref(false);

const getStatusLabel = (status) => {
  const map = {
    PRESENT: '已正常签到',
    LATE: '迟到',
    ABSENT: '缺勤',
    LEAVE: '已请假',
    正常: '已正常签到',
    迟到: '迟到',
    缺勤: '缺勤',
    请假: '已请假'
  };
  return map[status] || status || '状态待确认';
};

const getStatusType = (status) => {
  if (status === '正常' || status === 'PRESENT') return 'success';
  if (status === '迟到' || status === 'LATE') return 'warning';
  if (status === '缺勤' || status === 'ABSENT') return 'danger';
  if (status === '请假' || status === 'LEAVE') return 'info';
  return 'info';
};

const getStatusHint = (status) => {
  const map = {
    PRESENT: '本次课程已完成正常签到',
    LATE: '本次签到已记为迟到，建议关注后续课程签到时间',
    ABSENT: '系统已记为缺勤，如有特殊情况请及时说明',
    LEAVE: '本次课程已按请假处理',
    正常: '本次课程已完成正常签到',
    迟到: '本次签到已记为迟到，建议关注后续课程签到时间',
    缺勤: '系统已记为缺勤，如有特殊情况请及时说明',
    请假: '本次课程已按请假处理'
  };
  return map[status] || '可查看签到页或联系老师确认详情';
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await listMyAttendanceRecordsApi();
    list.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error(error);
    ElMessage.error('考勤记录加载失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

onMounted(loadData);
</script>

<style scoped>
.attendance-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #1a3557 0%, #31639f 55%, #57a7b7 100%);
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

.attendance-card {
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

.record-list {
  display: grid;
  gap: 14px;
}

.record-item {
  border: 1px solid #dbe7f2;
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  padding: 20px;
}

.record-top {
  display: flex;
  justify-content: space-between;
  gap: 14px;
}

.record-top h3 {
  margin: 0 0 8px;
}

.record-top p {
  margin: 0;
  color: #6e86a0;
}

.record-grid {
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

.info-block.wide {
  grid-column: span 1;
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
  .record-top,
  .card-header {
    flex-direction: column;
  }

  .record-grid {
    grid-template-columns: 1fr;
  }
}
</style>
