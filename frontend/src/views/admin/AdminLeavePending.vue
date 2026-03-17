<template>
  <div class="card">
    <h3>请假审批</h3>
    <button data-testid="admin-leave-refresh" class="refresh-btn" @click="loadData">刷新</button>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>学生ID</th>
          <th>类型</th>
          <th>开始</th>
          <th>结束</th>
          <th>原因</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.studentId }}</td>
          <td>{{ item.leaveType }}</td>
          <td>{{ item.startTime }}</td>
          <td>{{ item.endTime }}</td>
          <td>{{ item.reason }}</td>
          <td class="ops-cell">
            <button
              v-permission="'leave:approve'"
              :data-testid="`admin-approve-${item.id}`"
              @click="approve(item.id, 'APPROVED')"
            >
              通过
            </button>
            <button
              v-permission="'leave:approve'"
              :data-testid="`admin-reject-${item.id}`"
              @click="approve(item.id, 'REJECTED')"
            >
              驳回
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { approveLeaveApi, listPendingLeaveApi } from '../../api/leave';

const list = ref([]);

const loadData = async () => {
  list.value = await listPendingLeaveApi();
};

const approve = async (id, action) => {
  await approveLeaveApi(id, { action, comment: '' });
  await loadData();
};

onMounted(loadData);
</script>

<style scoped>
.refresh-btn {
  margin-bottom: 10px;
}

.ops-cell {
  display: flex;
  gap: 6px;
}
</style>
