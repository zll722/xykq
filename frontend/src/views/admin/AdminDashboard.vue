<template>
  <div class="admin-dashboard-v2">
    <section class="hero-panel">
      <div>
        <p class="hero-tag">管理总览</p>
        <h1>先处理高优先级事务，再看统计</h1>
        <p>把待审批、异常考勤和整体出勤放在一个视图里，减少在多个页面之间切换。</p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="router.push('/admin/leave-pending')">处理待审批</el-button>
        <el-button size="large" @click="router.push('/admin/attendance-exceptions')">查看异常考勤</el-button>
      </div>
    </section>

    <el-row :gutter="18" class="summary-grid">
      <el-col :xs="24" :sm="12" :xl="6">
        <el-card class="summary-card accent-primary" shadow="hover">
          <span class="summary-label">总考勤记录</span>
          <strong>{{ rate.total }}</strong>
          <p>累计采集的考勤记录总数</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :xl="6">
        <el-card class="summary-card accent-success" shadow="hover">
          <span class="summary-label">整体出勤率</span>
          <strong>{{ rate.presentRate }}%</strong>
          <p>正常出勤 {{ rate.presentCount }} 人次</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :xl="6">
        <el-card class="summary-card accent-warning" shadow="hover">
          <span class="summary-label">待审批请假</span>
          <strong>{{ pendingCount }}</strong>
          <p>{{ pendingCount > 0 ? '建议优先处理请假审批' : '当前没有待审批请假' }}</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :xl="6">
        <el-card class="summary-card accent-danger" shadow="hover">
          <span class="summary-label">异常考勤总量</span>
          <strong>{{ abnormalTotal }}</strong>
          <p>迟到、缺勤、请假、早退合计</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18">
      <el-col :xs="24" :xl="14">
        <el-card class="task-card" shadow="hover">
          <template #header>
            <div class="section-header">
              <div>
                <span class="section-title">优先处理事项</span>
                <p>真正需要管理员立即操作的工作放在最上面</p>
              </div>
            </div>
          </template>

          <div class="task-grid">
            <button class="task-button primary" @click="router.push('/admin/leave-pending')">
              <span>待审批请假</span>
              <small>{{ pendingCount }} 条待处理申请，点击直接进入审批</small>
            </button>
            <button class="task-button" @click="router.push('/admin/attendance-exceptions')">
              <span>异常考勤处理</span>
              <small>集中查看迟到、缺勤和异常状态记录</small>
            </button>
            <button class="task-button" @click="router.push('/admin/attendance-records')">
              <span>考勤记录检索</span>
              <small>按课程、班级和日期筛选历史考勤记录</small>
            </button>
            <button class="task-button" @click="router.push('/admin/stats')">
              <span>统计导出</span>
              <small>查看趋势并导出汇总报表</small>
            </button>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="10">
        <el-card class="insight-card" shadow="hover">
          <template #header>
            <div class="section-header compact">
              <div>
                <span class="section-title">关键提醒</span>
                <p>帮助你快速理解当前系统状态</p>
              </div>
            </div>
          </template>

          <ul class="insight-list">
            <li>迟到 {{ abnormal.lateCount }} 人次，缺勤 {{ abnormal.absentCount }} 人次</li>
            <li>请假 {{ abnormal.leaveCount }} 人次，早退 {{ abnormal.earlyLeaveCount }} 人次</li>
            <li>出勤率低于预期时，建议优先查看“统计分析”和“异常处理”</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18">
      <el-col :xs="24" :xl="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="section-header compact">
              <div>
                <span class="section-title">考勤状态分布</span>
                <p>快速了解正常、迟到、缺勤占比</p>
              </div>
            </div>
          </template>
          <div ref="rateChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :xl="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="section-header compact">
              <div>
                <span class="section-title">异常考勤类型</span>
                <p>识别最需要优先治理的问题类型</p>
              </div>
            </div>
          </template>
          <div ref="abnormalChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import { attendanceRateApi, attendanceAbnormalApi } from '../../api/adminStats';
import { listPendingLeaveApi } from '../../api/leave';

const router = useRouter();
const rate = ref({
  total: 0,
  presentCount: 0,
  lateCount: 0,
  absentCount: 0,
  presentRate: 0,
  lateRate: 0,
  absentRate: 0
});
const abnormal = ref({
  total: 0,
  lateCount: 0,
  absentCount: 0,
  leaveCount: 0,
  earlyLeaveCount: 0
});
const pendingCount = ref(0);

const rateChartRef = ref(null);
const abnormalChartRef = ref(null);
let rateChart;
let abnormalChart;

const abnormalTotal = computed(() => (
  Number(abnormal.value.lateCount || 0) +
  Number(abnormal.value.absentCount || 0) +
  Number(abnormal.value.leaveCount || 0) +
  Number(abnormal.value.earlyLeaveCount || 0)
));

const renderCharts = () => {
  if (!rateChart && rateChartRef.value) rateChart = echarts.init(rateChartRef.value);
  if (!abnormalChart && abnormalChartRef.value) abnormalChart = echarts.init(abnormalChartRef.value);

  rateChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0', left: 'center' },
    series: [{
      name: '考勤状态',
      type: 'pie',
      radius: ['42%', '72%'],
      data: [
        { value: rate.value.presentCount, name: '正常出勤', itemStyle: { color: '#2ca877' } },
        { value: rate.value.lateCount, name: '迟到', itemStyle: { color: '#d48819' } },
        { value: rate.value.absentCount, name: '缺勤', itemStyle: { color: '#d9485f' } }
      ]
    }]
  });

  abnormalChart?.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['迟到', '缺勤', '请假', '早退'] },
    yAxis: { type: 'value' },
    series: [{
      name: '人次',
      type: 'bar',
      barWidth: '40%',
      data: [
        { value: abnormal.value.lateCount, itemStyle: { color: '#d48819' } },
        { value: abnormal.value.absentCount, itemStyle: { color: '#d9485f' } },
        { value: abnormal.value.leaveCount, itemStyle: { color: '#7b8ea8' } },
        { value: abnormal.value.earlyLeaveCount, itemStyle: { color: '#5a8dee' } }
      ]
    }]
  });
};

const loadData = async () => {
  try {
    const [rateData, abnormalData, pending] = await Promise.all([
      attendanceRateApi({}),
      attendanceAbnormalApi({}),
      listPendingLeaveApi()
    ]);
    rate.value = rateData;
    abnormal.value = abnormalData;
    pendingCount.value = Array.isArray(pending) ? pending.length : 0;
    await nextTick();
    renderCharts();
  } catch (error) {
    console.error(error);
  }
};

const resizeCharts = () => {
  rateChart?.resize();
  abnormalChart?.resize();
};

onMounted(() => {
  loadData();
  window.addEventListener('resize', resizeCharts);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts);
  rateChart?.dispose();
  abnormalChart?.dispose();
});
</script>

<style scoped>
.admin-dashboard-v2 {
  display: grid;
  gap: 18px;
}

.hero-panel {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #112d49 0%, #1c4b83 55%, #2d768b 100%);
  color: #fff;
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.hero-panel h1 {
  margin: 0;
  color: #fff;
}

.hero-panel p {
  margin: 10px 0 0;
  max-width: 560px;
  color: rgba(255, 255, 255, 0.82);
}

.hero-actions {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.summary-card,
.task-card,
.insight-card,
.chart-card {
  border-radius: 24px;
  border: none;
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
.accent-danger { box-shadow: inset 0 4px 0 #d9485f; }

.section-header {
  display: flex;
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

.task-grid {
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

.insight-list {
  margin: 0;
  padding-left: 18px;
  display: grid;
  gap: 10px;
  color: #5f7893;
}
</style>
