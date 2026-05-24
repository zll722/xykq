<template>
  <div class="teacher-leave-notifications">
    <section class="page-hero">
      <div>
        <p class="page-tag">请假通知</p>
        <h1>您授课班级的审批通过请假</h1>
        <p>以下请假均已审批通过，请关注课堂出勤安排。</p>
      </div>
      <div class="hero-note">
        <strong>{{ list.length }}</strong>
        <span>条请假记录</span>
      </div>
    </section>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">请假通知列表</span>
            <p>涉及您所授课程的已审批请假，按审批时间倒序排列</p>
          </div>
          <el-button @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column label="申请学生" min-width="120">
          <template #default="{ row }">
            <span class="student-name">{{ row.studentName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属班级" min-width="160">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.className || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="涉及课程" min-width="160">
          <template #default="{ row }">
            <el-tag type="success" size="small">{{ row.courseName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeColor(row.leaveType)">{{ leaveTypeLabel(row.leaveType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假时间" min-width="240">
          <template #default="{ row }">
            <div class="time-cell">
              <strong>{{ row.startTime }}</strong>
              <span>至 {{ row.endTime }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="请假原因" min-width="220">
          <template #default="{ row }">
            <div class="reason-cell">{{ row.reason || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="审批时间" prop="approvedAt" min-width="160" />
        <template #empty>
          <el-empty description="暂无请假通知，您授课班级的学生目前没有已审批请假记录" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);

const loadData = async () => {
  loading.value = true;
  try {
    const data = await request.get('/teacher/leave-notifications');
    list.value = Array.isArray(data) ? data : [];
  } catch {
    ElMessage.error('加载请假通知失败');
  } finally {
    loading.value = false;
  }
};

const LEAVE_TYPE_MAP = { SICK: '病假', PERSONAL: '事假', PUBLIC: '公假', OTHER: '其他' };
const leaveTypeLabel = (type) => LEAVE_TYPE_MAP[type] || type || '其他';
const typeColor = (type) => {
  if (type === 'SICK') return 'danger';
  if (type === 'PERSONAL') return 'warning';
  if (type === 'PUBLIC') return 'success';
  return 'info';
};

onMounted(loadData);
</script>

<style scoped>
.teacher-leave-notifications {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.page-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 28px;
  padding: 32px 36px;
  border-radius: 24px;
  background: linear-gradient(135deg, #143a63 0%, #275f9d 100%);
  color: #fff;
}

.page-tag {
  margin: 0 0 10px;
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  opacity: 0.72;
}

.page-hero h1 {
  margin: 0 0 10px;
  font-size: 26px;
  font-weight: 700;
}

.page-hero p {
  margin: 0;
  opacity: 0.82;
}

.hero-note {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 18px 28px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.12);
  white-space: nowrap;
}

.hero-note strong {
  font-size: 28px;
  font-weight: 800;
}

.hero-note span {
  font-size: 13px;
  opacity: 0.8;
}

.table-card {
  border-radius: 18px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #15334f;
}

.card-header p {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b86a3;
}

.student-name {
  font-weight: 600;
  color: #15334f;
}

.time-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 13px;
}

.time-cell strong {
  color: #15334f;
}

.time-cell span {
  color: #6b86a3;
}

.reason-cell {
  font-size: 13px;
  color: #5f7893;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
