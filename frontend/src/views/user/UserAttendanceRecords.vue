<template>
  <div class="card">
    <h3 style="margin-top:0;">我的考勤记录</h3>
    <button @click="loadData" style="margin-bottom:10px;">刷新</button>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>课程</th>
          <th>日期</th>
          <th>签到时间</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.courseName }}</td>
          <td>{{ item.attendanceDate }}</td>
          <td>{{ item.signedAt || '-' }}</td>
          <td>{{ item.status }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listMyAttendanceRecordsApi } from '../../api/attendance';

const list = ref([]);

const loadData = async () => {
  list.value = await listMyAttendanceRecordsApi();
};

onMounted(loadData);
</script>
