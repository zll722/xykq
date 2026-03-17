<template>
  <div class="card">
    <h3 style="margin-top: 0;">班级管理</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;">
      <input v-model="form.classCode" placeholder="班级编码" />
      <input v-model="form.className" placeholder="班级名称" />
      <input v-model.number="form.capacity" type="number" placeholder="人数" />
      <button v-permission="'class:create'" @click="createClass">新增</button>
      <button @click="loadData">刷新</button>
    </div>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>编码</th>
          <th>名称</th>
          <th>人数</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.classCode }}</td>
          <td>{{ item.className }}</td>
          <td>{{ item.capacity }}</td>
          <td>{{ item.status }}</td>
          <td>
            <button v-permission="'class:delete'" @click="remove(item.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { createClassApi, deleteClassApi, listClassesApi } from '../../api/adminTeaching';

const list = ref([]);
const form = reactive({
  classCode: '',
  className: '',
  capacity: 30
});

const loadData = async () => {
  list.value = await listClassesApi();
};

const createClass = async () => {
  if (!form.classCode || !form.className) return;
  await createClassApi({
    classCode: form.classCode,
    className: form.className,
    capacity: form.capacity || 30,
    status: 1
  });
  form.classCode = '';
  form.className = '';
  await loadData();
};

const remove = async (id) => {
  await deleteClassApi(id);
  await loadData();
};

onMounted(loadData);
</script>
