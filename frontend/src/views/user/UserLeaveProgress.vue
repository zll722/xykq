<template>
  <div class="card">
    <h3 style="margin-top:0;">审批进度</h3>
    <button @click="loadData" style="margin-bottom:10px;">刷新</button>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>申请ID</th>
          <th>类型</th>
          <th>开始</th>
          <th>结束</th>
          <th>当前状态</th>
          <th>最近动作</th>
          <th>审批意见</th>
          <th>动作时间</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.requestId">
          <td>{{ item.requestId }}</td>
          <td>{{ item.leaveType }}</td>
          <td>{{ item.startTime }}</td>
          <td>{{ item.endTime }}</td>
          <td>{{ item.status }}</td>
          <td>{{ item.latestAction || '-' }}</td>
          <td>{{ item.latestComment || '-' }}</td>
          <td>{{ item.latestActedAt || '-' }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listMyLeaveProgressApi } from '../../api/leave';

const list = ref([]);
const loadData = async () => {
  list.value = await listMyLeaveProgressApi();
};
onMounted(loadData);
</script>
