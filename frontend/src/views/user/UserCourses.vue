<template>
  <div class="card">
    <h3 style="margin-top:0;">我的课程</h3>
    <button @click="loadData" style="margin-bottom:10px;">刷新</button>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>排班ID</th>
          <th>课程</th>
          <th>班级ID</th>
          <th>上课时间</th>
          <th>地点</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.scheduleId">
          <td>{{ item.scheduleId }}</td>
          <td>{{ item.courseName }}</td>
          <td>{{ item.classId }}</td>
          <td>周{{ item.weekDay }} {{ item.startTime }}-{{ item.endTime }}</td>
          <td>{{ item.location }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listMySchedulesApi } from '../../api/attendance';

const list = ref([]);

const loadData = async () => {
  list.value = await listMySchedulesApi();
};

onMounted(loadData);
</script>
