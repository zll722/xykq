<template>
  <div class="stats-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">统计分析</p>
        <h1>用业务语言筛选，再做导出</h1>
        <p>把筛选条件、图表解释和导出动作放在一个视图里，避免管理员面对一堆技术字段无从下手。</p>
      </div>
      <div class="hero-actions">
        <el-button @click="loadData">刷新数据</el-button>
        <el-button type="primary" @click="downloadSummary">导出汇总</el-button>
      </div>
    </section>

    <el-card class="filter-card" shadow="hover">
      <template #header>
        <div class="card-header compact">
          <div>
            <span class="title">筛选条件</span>
            <p>按课程、班级和时间范围缩小统计范围</p>
          </div>
        </div>
      </template>

      <el-form :inline="true" class="filter-form">
        <el-form-item label="课程ID">
          <el-input v-model="filters.courseId" placeholder="可选" />
        </el-form-item>
        <el-form-item label="班级ID">
          <el-input v-model="filters.classId" placeholder="可选" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="filters.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择开始日期" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="filters.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择结束日期" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="18">
      <el-col :xs="24" :xl="6">
        <el-card class="summary-card accent-primary" shadow="hover">
          <span class="summary-label">考勤总数</span>
          <strong>{{ rate.total }}</strong>
          <p>当前筛选范围内的总记录数</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :xl="6">
        <el-card class="summary-card accent-success" shadow="hover">
          <span class="summary-label">正常出勤率</span>
          <strong>{{ rate.presentRate }}%</strong>
          <p>正常 {{ rate.presentCount }} 人次</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :xl="6">
        <el-card class="summary-card accent-warning" shadow="hover">
          <span class="summary-label">迟到总量</span>
          <strong>{{ abnormal.lateCount }}</strong>
          <p>需要进一步关注课堂纪律</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :xl="6">
        <el-card class="summary-card accent-danger" shadow="hover">
          <span class="summary-label">缺勤总量</span>
          <strong>{{ abnormal.absentCount }}</strong>
          <p>建议结合异常处理进一步排查</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18">
      <el-col :xs="24" :xl="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header compact">
              <div>
                <span class="title">考勤状态分布</span>
                <p>比较正常、迟到、缺勤在当前筛选范围内的占比</p>
              </div>
            </div>
          </template>
          <div ref="rateChartRef" style="height: 280px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :xl="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header compact">
              <div>
                <span class="title">异常类型分布</span>
                <p>判断迟到、缺勤、请假和早退哪类问题最突出</p>
              </div>
            </div>
          </template>
          <div ref="abnormalChartRef" style="height: 280px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue';
import * as echarts from 'echarts';
import { attendanceAbnormalApi, attendanceRateApi, exportSummaryApi } from '../../api/adminStats';

const filters = reactive({
  courseId: '',
  classId: '',
  startDate: '',
  endDate: ''
});

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

const rateChartRef = ref(null);
const abnormalChartRef = ref(null);
let rateChart;
let abnormalChart;

const renderCharts = () => {
  if (!rateChart && rateChartRef.value) rateChart = echarts.init(rateChartRef.value);
  if (!abnormalChart && abnormalChartRef.value) abnormalChart = echarts.init(abnormalChartRef.value);

  rateChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0', left: 'center' },
    series: [{
      type: 'pie',
      radius: ['45%', '72%'],
      data: [
        { value: rate.value.presentCount, name: '正常' },
        { value: rate.value.lateCount, name: '迟到' },
        { value: rate.value.absentCount, name: '缺勤' }
      ]
    }]
  });

  abnormalChart?.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['迟到', '缺勤', '请假', '早退'] },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [
        abnormal.value.lateCount,
        abnormal.value.absentCount,
        abnormal.value.leaveCount,
        abnormal.value.earlyLeaveCount
      ],
      itemStyle: { color: '#205072' }
    }]
  });
};

const loadData = async () => {
  rate.value = await attendanceRateApi(filters);
  abnormal.value = await attendanceAbnormalApi(filters);
  await nextTick();
  renderCharts();
};

const downloadSummary = async () => {
  const blob = await exportSummaryApi(filters);
  const href = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = href;
  a.download = 'attendance_summary.xlsx';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(href);
};

const resizeCharts = () => {
  rateChart?.resize();
  abnormalChart?.resize();
};

onMounted(async () => {
  await loadData();
  window.addEventListener('resize', resizeCharts);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts);
  rateChart?.dispose();
  abnormalChart?.dispose();
});
</script>

<style scoped>
.stats-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
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

.page-hero h1 {
  margin: 0;
  color: #fff;
}

.page-hero p {
  margin: 10px 0 0;
  max-width: 620px;
  color: rgba(255, 255, 255, 0.82);
}

.hero-actions {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.filter-card,
.summary-card,
.chart-card {
  border-radius: 24px;
  border: none;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 0;
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
</style>
