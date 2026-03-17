<template>
  <div class="card">
    <h3 style="margin-top: 0;">课程管理</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;">
      <input v-model="form.courseCode" placeholder="课程编码" />
      <input v-model="form.courseName" placeholder="课程名称" />
      <input v-model.number="form.teacherId" type="number" placeholder="教师ID" />
      <input v-model="form.location" placeholder="地点" />
      <button v-permission="'course:create'" @click="createCourse">新增</button>
      <button @click="loadData">刷新</button>
    </div>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>编码</th>
          <th>名称</th>
          <th>教师ID</th>
          <th>上课时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.courseCode }}</td>
          <td>{{ item.courseName }}</td>
          <td>{{ item.teacherId }}</td>
          <td>{{ item.weekDay }} {{ item.startTime }}-{{ item.endTime }}</td>
          <td>
            <button v-permission="'course:delete'" @click="remove(item.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { createCourseApi, deleteCourseApi, listCoursesApi } from '../../api/adminTeaching';

const list = ref([]);
const form = reactive({
  courseCode: '',
  courseName: '',
  teacherId: 1,
  location: '',
  weekDay: 1,
  startTime: '08:00:00',
  endTime: '09:40:00'
});

const loadData = async () => {
  list.value = await listCoursesApi();
};

const createCourse = async () => {
  if (!form.courseCode || !form.courseName) return;
  await createCourseApi({
    courseCode: form.courseCode,
    courseName: form.courseName,
    teacherId: form.teacherId,
    location: form.location,
    weekDay: form.weekDay,
    startTime: form.startTime,
    endTime: form.endTime,
    status: 1
  });
  form.courseCode = '';
  form.courseName = '';
  await loadData();
};

const remove = async (id) => {
  await deleteCourseApi(id);
  await loadData();
};

onMounted(loadData);
</script>
