<template>
  <div class="leave-history-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">审批历史</p>
        <h1>直接看懂谁请假、谁审批、结果如何</h1>
        <p>不再展示一排审批人 ID 和申请 ID，而是回到真实业务语义：申请人、审批人、时段、结果与说明。</p>
      </div>
      <div class="hero-side">
        <strong>{{ list.length }}</strong>
        <span>条审批记录</span>
      </div>
    </section>

    <el-card class="history-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">审批流转记录</span>
            <p>适合追踪某条请假最终是被谁处理、何时处理、因为什么结论。</p>
          </div>
          <el-button @click="loadData">刷新</el-button>
        </div>
      </template>

      <el-table :data="list" border stripe>
        <el-table-column label="申请人" min-width="140">
          <template #default="{ row }">
            {{ row.studentName || `学生#${row.studentId}` }}
          </template>
        </el-table-column>
        <el-table-column label="请假类型" width="110">
          <template #default="{ row }">
            <el-tag>{{ row.leaveType || '未填写' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假时间" min-width="220">
          <template #default="{ row }">
            <div class="time-cell">
              <strong>{{ row.startTime || '-' }}</strong>
              <span>至 {{ row.endTime || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="审批结果" width="120">
          <template #default="{ row }">
            <el-tag :type="actionType(row.action)">{{ actionLabel(row.action) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="当前申请状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.requestStatus)">{{ statusLabel(row.requestStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批人" min-width="140">
          <template #default="{ row }">
            {{ row.approverName || `管理员#${row.approverId}` }}
          </template>
        </el-table-column>
        <el-table-column label="审批意见" min-width="240">
          <template #default="{ row }">
            <div class="comment-cell">{{ row.comment || '未填写审批意见' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="处理时间" prop="actedAt" width="170" />
        <el-table-column label="请假原因" min-width="260">
          <template #default="{ row }">
            <div class="comment-cell">{{ row.reason || '未填写请假原因' }}</div>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无审批历史记录" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listLeaveApprovalHistoryApi } from '../../api/leave';

const list = ref([]);

const actionLabel = (value) => {
  const map = {
    APPROVED: '已通过',
    REJECTED: '已驳回'
  };
  return map[value] || value || '未知';
};

const actionType = (value) => {
  const map = {
    APPROVED: 'success',
    REJECTED: 'danger'
  };
  return map[value] || 'info';
};

const statusLabel = (value) => {
  const map = {
    PENDING: '待审批',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    CANCELED: '已撤销'
  };
  return map[value] || value || '未知';
};

const statusType = (value) => {
  const map = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    CANCELED: 'info'
  };
  return map[value] || '';
};

const loadData = async () => {
  list.value = await listLeaveApprovalHistoryApi();
};

onMounted(loadData);
</script>

<style scoped>
.leave-history-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #1f2f53 0%, #365f9d 55%, #6aa8df 100%);
  color: #fff;
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.page-hero h1 {
  margin: 0;
  color: #fff;
}

.page-hero p {
  margin: 10px 0 0;
  max-width: 660px;
  color: rgba(255, 255, 255, 0.84);
}

.hero-side {
  min-width: 160px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.12);
}

.hero-side strong,
.hero-side span {
  display: block;
}

.hero-side strong {
  font-size: 32px;
}

.history-card {
  border-radius: 24px;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.card-header p {
  margin: 6px 0 0;
  color: #7890a8;
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: #17324d;
}

.time-cell strong,
.time-cell span {
  display: block;
}

.comment-cell,
.time-cell span {
  color: #6f86a0;
}
</style>
