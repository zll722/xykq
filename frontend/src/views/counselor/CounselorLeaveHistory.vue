<template>
  <div class="leave-history-page">
    <section class="page-hero">
      <div>
        <p class="page-tag">审批历史</p>
        <h1>已处理的请假记录</h1>
        <p>您已审批过的学生请假申请记录，按申请时间倒序排列。</p>
      </div>
      <div class="hero-note">
        <strong>{{ list.length }}</strong>
        <span>条历史记录</span>
      </div>
    </section>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">审批历史列表</span>
          </div>
          <div class="header-right">
            <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width:120px" @change="loadData">
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已驳回" value="REJECTED" />
            </el-select>
            <el-button @click="loadData"><el-icon><Refresh /></el-icon>刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredList" border stripe v-loading="loading">
        <el-table-column label="申请ID" prop="id" width="80" />
        <el-table-column label="学生" min-width="100">
          <template #default="{ row }">
            <div>
              <div>{{ row.studentName }}</div>
              <div style="font-size:12px;color:#6b86a3">{{ row.studentNo }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="班级" min-width="140">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.className || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="typeColor(row.leaveType)" size="small">{{ row.leaveType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假时间" min-width="200">
          <template #default="{ row }">
            <span>{{ row.startTime }}</span><span style="color:#aab;margin:0 4px">至</span><span>{{ row.endTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审批结果" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : 'danger'" size="small">
              {{ row.status === 'APPROVED' ? '已通过' : '已驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批意见" min-width="160">
          <template #default="{ row }">
            <span style="font-size:13px;color:#5f7893">{{ row.comment || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审批时间" prop="actedAt" min-width="150" />
        <template #empty>
          <el-empty description="暂无审批历史记录" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import request from '../../utils/request';

const list = ref([]);
const loading = ref(false);
const statusFilter = ref('');

const filteredList = computed(() => {
  if (!statusFilter.value) return list.value;
  return list.value.filter(r => r.status === statusFilter.value);
});

const typeColor = (type) => {
  if (type === '病假') return 'danger';
  if (type === '事假') return 'warning';
  if (type === '公假') return 'success';
  return 'info';
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await request.get('/counselor/leave/history');
    list.value = Array.isArray(data) ? data : [];
  } catch {
    ElMessage.error('加载失败');
  } finally {
    loading.value = false;
  }
};

onMounted(loadData);
</script>

<style scoped>
.leave-history-page { display: flex; flex-direction: column; gap: 0; }
.page-hero {
  display: flex; align-items: flex-start; justify-content: space-between; gap: 24px;
  margin-bottom: 28px; padding: 32px 36px; border-radius: 24px;
  background: linear-gradient(135deg, #143a63 0%, #275f9d 100%); color: #fff;
}
.page-tag { margin: 0 0 10px; font-size: 12px; letter-spacing: 0.1em; text-transform: uppercase; opacity: 0.72; }
.page-hero h1 { margin: 0 0 10px; font-size: 26px; font-weight: 700; }
.page-hero p { margin: 0; opacity: 0.82; }
.hero-note {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 18px 28px; border-radius: 18px; background: rgba(255,255,255,0.12); white-space: nowrap;
}
.hero-note strong { font-size: 32px; font-weight: 800; }
.hero-note span { font-size: 13px; opacity: 0.8; }
.table-card { border-radius: 18px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.card-title { font-size: 16px; font-weight: 600; color: #15334f; }
.header-right { display: flex; gap: 10px; align-items: center; }
</style>
