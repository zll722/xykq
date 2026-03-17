<template>
  <div class="card">
    <h3 style="margin-top:0;">请假记录</h3>
    <button @click="loadData" style="margin-bottom:10px;">刷新</button>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>类型</th>
          <th>开始</th>
          <th>结束</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.leaveType }}</td>
          <td>{{ item.startTime }}</td>
          <td>{{ item.endTime }}</td>
          <td>{{ item.status }}</td>
          <td>
            <button v-if="item.status === 'PENDING'" @click="cancel(item.id)">撤销</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { cancelLeaveApi, listMyLeaveApi } from '../../api/leave';

const list = ref([]);

const loadData = async () => {
  list.value = await listMyLeaveApi();
};

const cancel = async (id) => {
  await cancelLeaveApi(id);
  await loadData();
};

onMounted(loadData);
</script>
