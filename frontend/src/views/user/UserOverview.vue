<template>
  <div class="card">
    <h3 style="margin-top:0;">个人中心总览</h3>
    <div style="display:flex;gap:12px;flex-wrap:wrap;margin-bottom:12px;">
      <div class="card" style="min-width:220px;">
        <div>总考勤：{{ overview.totalCount }}</div>
        <div>出勤：{{ overview.presentCount }}</div>
        <div>迟到：{{ overview.lateCount }}</div>
        <div>缺勤：{{ overview.absentCount }}</div>
        <div>出勤率：{{ overview.presentRate }}%</div>
      </div>
      <div class="card" style="min-width:280px;">
        <div style="display:flex;gap:8px;flex-wrap:wrap;">
          <input v-model="filters.startDate" placeholder="开始日期(YYYY-MM-DD)" />
          <input v-model="filters.endDate" placeholder="结束日期(YYYY-MM-DD)" />
          <button @click="loadCalendar">查询日历</button>
        </div>
      </div>
    </div>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>日期</th>
          <th>课程</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in calendar" :key="`${item.attendanceDate}-${item.courseName}`">
          <td>{{ item.attendanceDate }}</td>
          <td>{{ item.courseName }}</td>
          <td>{{ item.status }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { myCalendarApi, myOverviewApi } from '../../api/attendance';

const overview = ref({
  totalCount: 0,
  presentCount: 0,
  lateCount: 0,
  absentCount: 0,
  presentRate: 0
});
const calendar = ref([]);
const filters = reactive({
  startDate: '',
  endDate: ''
});

const loadOverview = async () => {
  overview.value = await myOverviewApi();
};

const loadCalendar = async () => {
  calendar.value = await myCalendarApi(filters);
};

onMounted(async () => {
  await loadOverview();
  await loadCalendar();
});
</script>
