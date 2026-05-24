<template>
  <div class="leave-pending-page">
    <section class="page-hero">
      <div>
        <p class="page-tag">待审批请假</p>
        <h1>学生请假申请审批</h1>
        <p>以下请假申请来自您负责班级的学生，请及时处理。</p>
      </div>
      <div class="hero-note">
        <strong>{{ list.length }}</strong>
        <span>条待审批</span>
      </div>
    </section>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">待审批列表</span>
            <p>建议优先处理时间紧急的申请</p>
          </div>
          <el-button @click="loadData">
            <el-icon><Refresh /></el-icon>刷新
          </el-button>
        </div>
      </template>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column label="申请ID" prop="id" width="80" />
        <el-table-column label="申请学生" min-width="100">
          <template #default="{ row }">
            <div class="student-cell">
              <strong>{{ row.studentName }}</strong>
              <span class="student-no">{{ row.studentNo }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属班级" min-width="150">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.className || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假类型" width="90">
          <template #default="{ row }">
            <el-tag :type="typeColor(row.leaveType)" size="small">{{ row.leaveType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假时间" min-width="220">
          <template #default="{ row }">
            <div class="time-cell">
              <span>{{ row.startTime }}</span>
              <span class="sep"> 至 </span>
              <span>{{ row.endTime }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="请假原因" min-width="200">
          <template #default="{ row }">
            <div class="reason-cell">{{ row.reason }}</div>
          </template>
        </el-table-column>
        <el-table-column label="证明材料" width="90">
          <template #default="{ row }">
            <el-button
              v-if="row.proofUrl"
              type="primary" link size="small"
              @click="previewProof(row.proofUrl)"
            >查看</el-button>
            <span v-else class="no-proof">无</span>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" prop="submittedAt" min-width="150" />
        <el-table-column label="审批操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="openApprove(row, 'APPROVED')">通过</el-button>
            <el-button type="danger" size="small" @click="openApprove(row, 'REJECTED')">驳回</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="当前没有待审批请假，您负责班级的学生暂无待处理申请" />
        </template>
      </el-table>
    </el-card>

    <!-- 审批弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="学生">
          <span>{{ currentRow?.studentName }} ({{ currentRow?.studentNo }})</span>
        </el-form-item>
        <el-form-item label="请假类型">
          <el-tag :type="typeColor(currentRow?.leaveType)">{{ currentRow?.leaveType }}</el-tag>
        </el-form-item>
        <el-form-item label="请假时间">
          <span>{{ currentRow?.startTime }} 至 {{ currentRow?.endTime }}</span>
        </el-form-item>
        <el-form-item label="备注意见">
          <el-input v-model="approveForm.comment" type="textarea" :rows="3" placeholder="可填写审批意见（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          :type="approveAction === 'APPROVED' ? 'success' : 'danger'"
          :loading="submitting"
          @click="submitApprove"
        >{{ approveAction === 'APPROVED' ? '确认通过' : '确认驳回' }}</el-button>
      </template>
    </el-dialog>

    <!-- 证明材料预览：图片直接展示，其他文件提供下载链接 -->
    <el-dialog v-model="proofVisible" title="证明材料" width="600px">
      <template v-if="isImageProof">
        <el-image :src="proofSrc" style="width:100%" fit="contain" />
      </template>
      <template v-else>
        <div style="text-align:center;padding:24px 0;">
          <p style="margin-bottom:16px;color:#5f7893;">该文件为非图片格式，请点击下载后查看</p>
          <el-button type="primary" :href="proofSrc" tag="a" target="_blank" download>
            下载查看证明材料
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const submitting = ref(false);
const currentRow = ref(null);
const approveAction = ref('APPROVED');
const approveForm = ref({ comment: '' });
const proofVisible = ref(false);
const proofSrc = ref('');

const IMAGE_EXTS = new Set(['jpg', 'jpeg', 'png', 'gif', 'webp']);
const isImageProof = computed(() => {
  const ext = proofSrc.value.split('.').pop()?.toLowerCase() || '';
  return IMAGE_EXTS.has(ext);
});

const dialogTitle = ref('');

const typeColor = (type) => {
  if (type === '病假') return 'danger';
  if (type === '事假') return 'warning';
  if (type === '公假') return 'success';
  return 'info';
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await request.get('/counselor/leave/pending');
    list.value = Array.isArray(data) ? data : [];
  } catch {
    ElMessage.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

const openApprove = (row, action) => {
  currentRow.value = row;
  approveAction.value = action;
  approveForm.value = { comment: '' };
  dialogTitle.value = action === 'APPROVED' ? '审批通过确认' : '驳回确认';
  dialogVisible.value = true;
};

const submitApprove = async () => {
  submitting.value = true;
  try {
    await request.post(`/counselor/leave/${currentRow.value.id}/approve`, {
      action: approveAction.value,
      comment: approveForm.value.comment
    });
    ElMessage.success(approveAction.value === 'APPROVED' ? '已通过审批' : '已驳回申请');
    dialogVisible.value = false;
    loadData();
  } catch (e) {
    ElMessage.error(e?.message || '操作失败');
  } finally {
    submitting.value = false;
  }
};

const previewProof = (url) => {
  proofSrc.value = url;
  proofVisible.value = true;
};

onMounted(loadData);
</script>

<style scoped>
.leave-pending-page { display: flex; flex-direction: column; gap: 0; }

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

.page-tag { margin: 0 0 10px; font-size: 12px; letter-spacing: 0.1em; text-transform: uppercase; opacity: 0.72; }
.page-hero h1 { margin: 0 0 10px; font-size: 26px; font-weight: 700; }
.page-hero p { margin: 0; opacity: 0.82; }

.hero-note {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 18px 28px;
  border-radius: 18px;
  background: rgba(255,255,255,0.12);
  white-space: nowrap;
}
.hero-note strong { font-size: 32px; font-weight: 800; }
.hero-note span { font-size: 13px; opacity: 0.8; }

.table-card { border-radius: 18px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.card-title { font-size: 16px; font-weight: 600; color: #15334f; }
.card-header p { margin: 4px 0 0; font-size: 13px; color: #6b86a3; }

.student-cell { display: flex; flex-direction: column; gap: 2px; }
.student-no { font-size: 12px; color: #6b86a3; }

.time-cell { font-size: 13px; color: #344054; }
.sep { color: #aab; margin: 0 2px; }

.reason-cell {
  font-size: 13px;
  color: #5f7893;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.no-proof { font-size: 12px; color: #aab; }
</style>
