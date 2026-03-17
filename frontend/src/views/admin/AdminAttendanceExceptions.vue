<template>
  <div class="card">
    <h3 style="margin-top:0;">考勤异常处理</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px;">
      <input v-model.number="filters.courseId" type="number" placeholder="课程ID" />
      <input v-model.number="filters.classId" type="number" placeholder="班级ID" />
      <input v-model="filters.attendanceDate" placeholder="日期(YYYY-MM-DD，可空)" />
      <button @click="loadData">查询异常</button>
    </div>

    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>课程</th>
          <th>班级</th>
          <th>学生ID</th>
          <th>日期</th>
          <th>当前状态</th>
          <th>处理状态</th>
          <th>原因</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.courseName }}</td>
          <td>{{ item.className }}</td>
          <td>{{ item.studentId }}</td>
          <td>{{ item.attendanceDate }}</td>
          <td>{{ item.status }}</td>
          <td>
            <input v-model="resolveState[item.id].afterStatus" placeholder="PRESENT/LATE/ABSENT/LEAVE" />
          </td>
          <td>
            <input v-model="resolveState[item.id].reason" placeholder="处理原因" />
          </td>
          <td>
            <button v-permission="'attendance:resolve'" @click="resolve(item.id)">确认修正</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { listAttendanceExceptionsApi, resolveAttendanceExceptionApi } from '../../api/adminAttendanceManage';

const list = ref([]);
const filters = reactive({
  courseId: '',
  classId: '',
  attendanceDate: ''
});
const resolveState = reactive({});

const loadData = async () => {
  list.value = await listAttendanceExceptionsApi(filters);
  list.value.forEach((item) => {
    if (!resolveState[item.id]) {
      resolveState[item.id] = { afterStatus: 'PRESENT', reason: '' };
    }
  });
};

const resolve = async (id) => {
  const payload = resolveState[id];
  await resolveAttendanceExceptionApi(id, payload);
  await loadData();
};
</script>
