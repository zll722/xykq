<template>
  <div class="card">
    <h3 style="margin-top:0;">Statistics</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;">
      <input v-model.number="filters.courseId" type="number" placeholder="Course ID" />
      <input v-model.number="filters.classId" type="number" placeholder="Class ID" />
      <input v-model="filters.startDate" placeholder="Start Date (YYYY-MM-DD)" />
      <input v-model="filters.endDate" placeholder="End Date (YYYY-MM-DD)" />
      <button @click="loadData">Refresh</button>
      <button v-permission="'stats:export'" @click="downloadSummary">Export Summary</button>
    </div>

    <div style="display:flex;gap:12px;flex-wrap:wrap;">
      <div class="card" style="min-width:360px;flex:1;">
        <h4 style="margin-top:0;">Attendance Rate</h4>
        <div ref="rateChartRef" style="height:260px;"></div>
      </div>
      <div class="card" style="min-width:360px;flex:1;">
        <h4 style="margin-top:0;">Abnormal Distribution</h4>
        <div ref="abnormalChartRef" style="height:260px;"></div>
      </div>
    </div>
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
  if (!rateChart && rateChartRef.value) {
    rateChart = echarts.init(rateChartRef.value);
  }
  if (!abnormalChart && abnormalChartRef.value) {
    abnormalChart = echarts.init(abnormalChartRef.value);
  }

  if (rateChart) {
    rateChart.setOption({
      tooltip: { trigger: 'item' },
      series: [
        {
          type: 'pie',
          radius: ['45%', '70%'],
          data: [
            { value: rate.value.presentCount, name: 'PRESENT' },
            { value: rate.value.lateCount, name: 'LATE' },
            { value: rate.value.absentCount, name: 'ABSENT' }
          ]
        }
      ]
    });
  }

  if (abnormalChart) {
    abnormalChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['LATE', 'ABSENT', 'LEAVE', 'EARLY_LEAVE']
      },
      yAxis: { type: 'value' },
      series: [
        {
          type: 'bar',
          data: [
            abnormal.value.lateCount,
            abnormal.value.absentCount,
            abnormal.value.leaveCount,
            abnormal.value.earlyLeaveCount
          ],
          itemStyle: { color: '#205072' }
        }
      ]
    });
  }
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

onMounted(async () => {
  await loadData();
  window.addEventListener('resize', resizeCharts);
});

const resizeCharts = () => {
  rateChart?.resize();
  abnormalChart?.resize();
};

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts);
  rateChart?.dispose();
  abnormalChart?.dispose();
});
</script>
