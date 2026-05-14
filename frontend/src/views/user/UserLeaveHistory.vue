<template>
  <div class="leave-history-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">请假记录</p>
        <h1>把你提交过的申请按业务语义整理出来</h1>
        <p>不再让编号占主视图，直接看类型、时间、状态，以及哪些申请还可以撤销。</p>
      </div>
      <div class="hero-side">
        <strong>{{ list.length }}</strong>
        <span>条历史申请</span>
      </div>
    </section>

    <el-card class="history-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">我的请假历史</span>
            <p>如果状态仍是待审批，可以直接撤销后重新提交更准确的信息。</p>
          </div>
          <el-button @click="loadData" :loading="loading">刷新</el-button>
        </div>
      </template>

      <div v-if="list.length" class="history-list" v-loading="loading">
        <article v-for="item in list" :key="item.id || item.requestId" class="history-item">
          <div class="history-top">
            <div>
              <h3>{{ item.leaveType || '未填写类型' }}</h3>
              <p>{{ formatRange(item.startTime, item.endTime) }}</p>
            </div>
            <div class="history-actions">
              <el-tag :type="statusType(item.status)">{{ statusLabel(item.status) }}</el-tag>
              <el-button v-if="item.status === 'PENDING'" type="danger" plain @click="cancel(item.id)">撤销申请</el-button>
            </div>
          </div>

          <div class="history-grid">
            <div class="info-block">
              <span>提交时间</span>
              <strong>{{ item.submittedAt || item.createdAt || '刚刚提交' }}</strong>
            </div>
            <div class="info-block">
              <span>证明材料</span>
              <strong>{{ item.attachmentUrl ? '已上传材料' : '未上传材料' }}</strong>
            </div>
            <div class="info-block wide">
              <span>请假原因</span>
              <strong>{{ item.reason || statusHint(item.status) }}</strong>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-else :description="loading ? '正在加载请假记录' : '你还没有历史请假记录。需要请假时，可直接前往请假中心提交。'">
        <div class="empty-actions">
          <el-button type="primary" @click="router.push('/user/leave-apply')">去请假中心</el-button>
          <el-button @click="router.push('/user/leave-progress')">查看审批进度</el-button>
        </div>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { cancelLeaveApi, listMyLeaveApi } from '../../api/leave';

const router = useRouter();
const list = ref([]);
const loading = ref(false);

const statusLabel = (value) => {
  const map = {
    PENDING: '待审批',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    CANCELED: '已撤销'
  };
  return map[value] || value || '处理中';
};

const statusType = (value) => {
  const map = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    CANCELED: 'info'
  };
  return map[value] || 'info';
};

const statusHint = (status) => {
  const map = {
    PENDING: '申请正在等待老师审批',
    APPROVED: '申请已通过，可按时间安排请假',
    REJECTED: '申请未通过，建议补充更完整的说明后重新提交',
    CANCELED: '这条申请已撤销，不会继续审批'
  };
  return map[status] || '可进入审批进度查看更多细节';
};

const formatRange = (startTime, endTime) => {
  if (!startTime && !endTime) return '请假时间待确认';
  return `${startTime || '-'} 至 ${endTime || '-'}`;
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await listMyLeaveApi();
    list.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error(error);
    ElMessage.error('请假记录加载失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const cancel = async (id) => {
  try {
    await ElMessageBox.confirm('撤销后这条申请将不再继续审批，是否继续？', '撤销申请', {
      type: 'warning'
    });
    await cancelLeaveApi(id);
    ElMessage.success('申请已撤销');
    await loadData();
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error);
      ElMessage.error('撤销失败，请稍后重试');
    }
  }
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
  background: linear-gradient(135deg, #1a355b 0%, #3267a6 55%, #5da6d9 100%);
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

.history-list {
  display: grid;
  gap: 14px;
}

.history-item {
  border: 1px solid #dbe7f2;
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  padding: 20px;
}

.history-top {
  display: flex;
  justify-content: space-between;
  gap: 14px;
}

.history-top h3 {
  margin: 0 0 8px;
}

.history-top p {
  margin: 0;
  color: #6e86a0;
}

.history-actions {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  flex-wrap: wrap;
}

.history-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.info-block {
  padding: 14px 16px;
  border-radius: 18px;
  background: #f3f8ff;
}

.info-block.wide {
  grid-column: span 1;
}

.info-block span,
.info-block strong {
  display: block;
}

.info-block span {
  color: #7890a8;
  font-size: 13px;
}

.info-block strong {
  margin-top: 6px;
  color: #17324d;
}

.empty-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 12px;
}

@media (max-width: 900px) {
  .page-hero,
  .history-top,
  .card-header {
    flex-direction: column;
  }

  .history-grid {
    grid-template-columns: 1fr;
  }
}
</style>
