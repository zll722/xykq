<template>
  <div class="leave-pending-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">待审批请假</p>
        <h1>把审批判断需要的信息一次给全</h1>
        <p>审批时至少要知道请假类型、时长、学生、原因和材料情况，避免管理员反复切页确认。</p>
      </div>
      <div class="hero-side">
        <strong>{{ list.length }}</strong>
        <span>条待审批申请</span>
      </div>
    </section>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">待审批列表</span>
            <p>建议优先处理时间紧急或原因描述充分的申请</p>
          </div>
          <el-button @click="loadData">刷新</el-button>
        </div>
      </template>

      <el-table :data="list" border stripe>
        <el-table-column label="申请ID" prop="id" width="80" />
        <el-table-column label="申请学生" min-width="110">
          <template #default="scope">
            <span class="student-name">{{ scope.row.studentName || `学生#${scope.row.studentId}` }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属班级" min-width="160">
          <template #default="scope">
            <el-tag type="info" size="small">{{ scope.row.className || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="辅导员" min-width="110">
          <template #default="scope">
            <span class="counselor-name">{{ scope.row.counselorName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="请假类型" width="90">
          <template #default="scope">
            <el-tag>{{ scope.row.leaveType || '未填写' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假时间" min-width="220">
          <template #default="scope">
            <div class="time-cell">
              <strong>{{ formatTime(scope.row.startTime) }}</strong>
              <span>至 {{ formatTime(scope.row.endTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="请假原因" min-width="220">
          <template #default="scope">
            <div class="reason-cell">{{ scope.row.reason || '未填写原因' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="审批动作" width="200" fixed="right">
          <template #default="scope">
            <div class="ops-cell">
              <el-button type="success" size="small" @click="approve(scope.row.id, 'APPROVED')">通过</el-button>
              <el-button size="small" @click="openReject(scope.row.id)">驳回</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!list.length" description="当前没有待审批请假，管理员可以回到总览页查看异常考勤或统计结果。" />
    </el-card>

    <el-dialog v-model="rejectVisible" title="驳回请假申请" width="480px">
      <el-form label-position="top">
        <el-form-item label="驳回说明">
          <el-input v-model="rejectComment" type="textarea" :rows="4" maxlength="120" show-word-limit placeholder="例如：材料不完整、时间范围不合理，请补充后重新提交" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { approveLeaveApi, listPendingLeaveApi } from '../../api/leave';

const list = ref([]);
const rejectVisible = ref(false);
const rejectComment = ref('');
const currentRejectId = ref(null);

const formatTime = (value) => {
  if (!value) return '未设置';
  return String(value).replace('T', ' ');
};

const loadData = async () => {
  list.value = await listPendingLeaveApi();
};

const approve = async (id, action, comment = '') => {
  await approveLeaveApi(id, { action, comment });
  ElMessage.success(action === 'APPROVED' ? '已通过该请假申请' : '已驳回该请假申请');
  await loadData();
};

const openReject = (id) => {
  currentRejectId.value = id;
  rejectComment.value = '';
  rejectVisible.value = true;
};

const confirmReject = async () => {
  await approve(currentRejectId.value, 'REJECTED', rejectComment.value);
  rejectVisible.value = false;
};

onMounted(loadData);
</script>

<style scoped>
.leave-pending-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #112d49 0%, #1c4b83 55%, #2d768b 100%);
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
  max-width: 600px;
  color: rgba(255, 255, 255, 0.82);
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

.table-card {
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

.time-cell span,
.reason-cell {
  color: #6f86a0;
}

.student-name {
  font-weight: 600;
  color: #17324d;
}

.counselor-name {
  color: #5f7893;
  font-size: 13px;
}

.ops-cell {
  display: flex;
  gap: 8px;
}
</style>
