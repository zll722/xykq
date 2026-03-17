<template>
  <div class="card">
    <h3 style="margin-top:0;">审批历史</h3>
    <button @click="loadData" style="margin-bottom:10px;">刷新</button>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>申请ID</th>
          <th>审批人ID</th>
          <th>动作</th>
          <th>意见</th>
          <th>时间</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.leaveRequestId }}</td>
          <td>{{ item.approverId }}</td>
          <td>{{ item.action }}</td>
          <td>{{ item.comment || '-' }}</td>
          <td>{{ item.actedAt }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listLeaveApprovalHistoryApi } from '../../api/leave';

const list = ref([]);

const loadData = async () => {
  list.value = await listLeaveApprovalHistoryApi();
};

onMounted(loadData);
</script>
