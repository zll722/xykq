<template>
  <div class="card">
    <h3 style="margin-top: 0;">排班管理</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;">
      <input v-model.number="form.courseId" type="number" placeholder="课程ID" />
      <input v-model.number="form.classId" type="number" placeholder="班级ID" />
      <input v-model.number="form.weekNo" type="number" placeholder="周次(可空)" />
      <button v-permission="'schedule:create'" @click="createSchedule">新增</button>
      <button @click="loadData">刷新</button>
    </div>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>课程ID</th>
          <th>班级ID</th>
          <th>周次</th>
          <th>时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.courseId }}</td>
          <td>{{ item.classId }}</td>
          <td>{{ item.weekNo || '-' }}</td>
          <td>{{ item.weekDay }} {{ item.startTime }}-{{ item.endTime }}</td>
          <td>
            <button v-permission="'schedule:delete'" @click="remove(item.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { createScheduleApi, deleteScheduleApi, listSchedulesApi } from '../../api/adminTeaching';

const list = ref([]);
const form = reactive({
  courseId: 1,
  classId: 1,
  weekNo: null,
  weekDay: 1,
  startTime: '08:00:00',
  endTime: '09:40:00',
  location: ''
});

const loadData = async () => {
  list.value = await listSchedulesApi();
};

const createSchedule = async () => {
  if (!form.courseId || !form.classId) return;
  await createScheduleApi({
    courseId: form.courseId,
    classId: form.classId,
    weekNo: form.weekNo,
    weekDay: form.weekDay,
    startTime: form.startTime,
    endTime: form.endTime,
    location: form.location
  });
  await loadData();
};

const remove = async (id) => {
  await deleteScheduleApi(id);
  await loadData();
};

onMounted(loadData);
</script>
