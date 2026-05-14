<template>
  <div class="leave-progress-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">审批进度</p>
        <h1>每一条申请现在走到哪一步，一眼就能看懂</h1>
        <p>不再只看状态码和申请编号，而是直接看到请假类型、时间、最新处理结果和下一步建议。</p>
      </div>
      <div class="hero-side">
        <strong>{{ list.length }}</strong>
        <span>条申请进度</span>
      </div>
    </section>

    <el-card class="progress-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">我的审批流转</span>
            <p>优先关注最近一条，尤其是被驳回、待补充说明或已通过的申请。</p>
          </div>
          <el-button @click="loadData" :loading="loading">刷新</el-button>
        </div>
      </template>

      <div v-if="list.length" class="progress-list" v-loading="loading">
        <article v-for="item in list" :key="item.requestId || item.id" class="progress-item">
          <div class="progress-main">
            <div class="progress-top">
              <div>
                <h3>{{ item.leaveType || '未填写类型' }}</h3>
                <p>{{ formatRange(item.startTime, item.endTime) }}</p>
              </div>
              <div class="tag-group">
                <el-tag :type="statusType(item.status)">{{ statusLabel(item.status) }}</el-tag>
                <el-tag v-if="item.latestAction" :type="actionType(item.latestAction)" effect="plain">
                  {{ actionLabel(item.latestAction) }}
                </el-tag>
              </div>
            </div>

            <div class="progress-grid">
              <div class="info-block">
                <span>提交时间</span>
                <strong>{{ item.submittedAt || '刚刚提交' }}</strong>
              </div>
              <div class="info-block">
                <span>最近处理</span>
                <strong>{{ item.latestActedAt || '暂未处理' }}</strong>
              </div>
              <div class="info-block wide">
                <span>审批意见</span>
                <strong>{{ item.latestComment || statusHint(item.status) }}</strong>
              </div>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-else :description="loading ? '正在加载审批进度' : '你还没有任何请假申请，提交后这里会持续显示处理进度。'">
        <div class="empty-actions">
          <el-button type="primary" @click="router.push('/user/leave-apply')">去提交请假</el-button>
          <el-button @click="router.push('/user/leave-history')">查看历史记录</el-button>
        </div>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { listMyLeaveProgressApi } from '../../api/leave';

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

const actionLabel = (value) => {
  const map = {
    APPROVED: '最近已通过',
    REJECTED: '最近已驳回'
  };
  return map[value] || value || '暂无动作';
};

const actionType = (value) => {
  const map = {
    APPROVED: 'success',
    REJECTED: 'danger'
  };
  return map[value] || 'info';
};

const statusHint = (status) => {
  const map = {
    PENDING: '老师还在处理中，请耐心等待结果',
    APPROVED: '申请已通过，可按请假时间安排后续事项',
    REJECTED: '建议查看原因后重新提交更完整的说明',
    CANCELED: '这条申请已由你撤销，不会继续进入审批'
  };
  return map[status] || '可返回请假中心继续处理';
};

const formatRange = (startTime, endTime) => {
  if (!startTime && !endTime) return '请假时间待确认';
  return `${startTime || '-'} 至 ${endTime || '-'}`;
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await listMyLeaveProgressApi();
    list.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error(error);
    ElMessage.error('审批进度加载失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

onMounted(loadData);
</script>

<style scoped>
.leave-progress-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #173154 0%, #2f5f9e 52%, #55a27a 100%);
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

.progress-card {
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

.progress-list {
  display: grid;
  gap: 14px;
}

.progress-item {
  border: 1px solid #dbe7f2;
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  padding: 20px;
}

.progress-top {
  display: flex;
  justify-content: space-between;
  gap: 14px;
}

.progress-top h3 {
  margin: 0 0 8px;
}

.progress-top p {
  margin: 0;
  color: #6e86a0;
}

.tag-group {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  flex-wrap: wrap;
}

.progress-grid {
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
  .progress-top,
  .card-header {
    flex-direction: column;
  }

  .progress-grid {
    grid-template-columns: 1fr;
  }
}
</style>
