<template>
  <div class="card">
    <h3 style="margin-top:0;">考勤记录管理</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px;">
      <input v-model.number="filters.courseId" type="number" placeholder="课程ID" />
      <input v-model.number="filters.classId" type="number" placeholder="班级ID" />
      <input v-model="filters.attendanceDate" placeholder="日期(YYYY-MM-DD)" />
      <input v-model="filters.status" placeholder="状态(PRESENT/LATE/ABSENT)" />
      <button @click="loadData">查询</button>
      <button v-permission="'stats:export'" @click="download">导出Excel</button>
    </div>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>课程</th>
          <th>班级</th>
          <th>学生ID</th>
          <th>日期</th>
          <th>签到时间</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.courseName }}</td>
          <td>{{ item.className }}</td>
          <td>{{ item.studentId }}</td>
          <td>{{ item.attendanceDate }}</td>
          <td>{{ item.signedAt || '-' }}</td>
          <td>{{ item.status }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { exportAttendanceApi, listAdminAttendanceRecordsApi } from '../../api/adminStats';

const list = ref([]);
const filters = reactive({
  courseId: '',
  classId: '',
  attendanceDate: '',
  status: ''
});

const loadData = async () => {
  list.value = await listAdminAttendanceRecordsApi(filters);
};

const download = async () => {
  const blob = await exportAttendanceApi({
    courseId: filters.courseId,
    classId: filters.classId,
    startDate: filters.attendanceDate,
    endDate: filters.attendanceDate
  });
  const href = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = href;
  a.download = 'attendance_export.xlsx';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(href);
};

onMounted(loadData);
</script>
