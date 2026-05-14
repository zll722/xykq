<template>
  <div class="message-center-page">
    <section class="message-hero">
      <div>
        <p class="hero-tag">消息中心</p>
        <h1>把需要你处理的结果集中起来</h1>
        <p>审批结果、签到提醒、系统通知都放在这里；如果没有消息，也会明确告诉你下一步该去哪里。</p>
      </div>
      <div class="message-actions">
        <el-button @click="loadData">刷新</el-button>
        <el-button type="primary" @click="markAllRead">全部标记已读</el-button>
      </div>
    </section>

    <el-row :gutter="18">
      <el-col :xs="24" :xl="8">
        <el-card class="overview-card" shadow="hover">
          <div class="message-stat">
            <span>未读消息</span>
            <strong>{{ unreadCount }}</strong>
          </div>
          <div class="message-stat muted">
            <span>最新消息时间</span>
            <strong>{{ latestTime }}</strong>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :xl="16">
        <el-card class="list-card" shadow="hover">
          <template #header>
            <div class="list-header">
              <div>
                <span class="title">消息列表</span>
                <p>优先查看未读消息，避免错过审批结果和签到提醒</p>
              </div>
            </div>
          </template>

          <div v-if="messages.length" class="message-list">
            <article v-for="item in messages" :key="item.id" class="message-item" :class="{ unread: item.readFlag !== 1 }">
              <div class="message-top">
                <div>
                  <div class="message-meta">
                    <el-tag size="small" effect="plain" :type="messageTypeTag(item.messageType)">
                      {{ messageTypeLabel(item.messageType) }}
                    </el-tag>
                    <span class="message-time">{{ item.createdAt || item.sentAt || '刚刚' }}</span>
                  </div>
                  <h3>{{ item.title || '系统消息' }}</h3>
                  <p>{{ item.content || '暂无详细内容' }}</p>
                </div>
                <el-tag :type="item.readFlag === 1 ? 'info' : 'danger'">{{ item.readFlag === 1 ? '已读' : '未读' }}</el-tag>
              </div>
              <div class="message-bottom">
                <span>{{ messageActionHint(item.messageType) }}</span>
              </div>
            </article>
          </div>
          <el-empty v-else description="暂时没有任何消息。你可以去签到中心查看今日课程，或去请假中心提交新的申请。">
            <div class="empty-actions">
              <el-button @click="router.push('/user/sign-in')">去签到中心</el-button>
              <el-button type="primary" @click="router.push('/user/leave-apply')">去请假中心</el-button>
            </div>
          </el-empty>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { listMessagesApi, readAllMessageApi } from '../../api/message';

const router = useRouter();
const messages = ref([]);

const unreadCount = computed(() => messages.value.filter((item) => item.readFlag !== 1).length);
const latestTime = computed(() => messages.value[0]?.createdAt || messages.value[0]?.sentAt || '暂无消息');

const messageTypeLabel = (value) => {
  const map = {
    LEAVE_RESULT: '请假结果',
    ATT_EXCEPTION: '考勤异常',
    SIGN_REMIND: '签到提醒',
    SYSTEM: '系统通知'
  };
  return map[value] || value || '通知';
};

const messageTypeTag = (value) => {
  const map = {
    LEAVE_RESULT: 'success',
    ATT_EXCEPTION: 'danger',
    SIGN_REMIND: 'warning',
    SYSTEM: 'info'
  };
  return map[value] || 'info';
};

const messageActionHint = (value) => {
  const map = {
    LEAVE_RESULT: '建议优先确认审批结果，必要时进入请假记录查看详情',
    ATT_EXCEPTION: '建议尽快核对对应课程签到情况，避免遗漏说明',
    SIGN_REMIND: '如果当前课程可签到，建议尽快前往签到中心处理',
    SYSTEM: '这是一条系统通知，可按需要查看并处理'
  };
  return map[value] || '可根据消息内容决定下一步操作';
};

const loadData = async () => {
  try {
    const data = await listMessagesApi();
    messages.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error(error);
    ElMessage.error('消息加载失败，请稍后重试');
  }
};

const markAllRead = async () => {
  try {
    await readAllMessageApi();
    ElMessage.success('已将当前消息标记为已读');
    loadData();
  } catch (error) {
    console.error(error);
    ElMessage.error('操作失败，请稍后重试');
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.message-center-page {
  display: grid;
  gap: 18px;
}

.message-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #153a63 0%, #275f9d 55%, #4ca59f 100%);
  color: #fff;
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.message-hero h1 {
  margin: 0;
  color: #fff;
}

.message-hero p {
  max-width: 600px;
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.message-actions {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.overview-card,
.list-card {
  border-radius: 24px;
  border: none;
}

.overview-card :deep(.el-card__body) {
  display: grid;
  gap: 16px;
}

.message-stat {
  padding: 18px;
  border-radius: 20px;
  background: linear-gradient(180deg, #f5f9ff 0%, #edf4fb 100%);
}

.message-stat span,
.message-stat strong {
  display: block;
}

.message-stat span {
  color: #7188a1;
}

.message-stat strong {
  margin-top: 8px;
  font-size: 28px;
  color: #17324d;
}

.message-stat.muted strong {
  font-size: 18px;
}

.list-header p {
  margin: 6px 0 0;
  color: #7890a8;
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: #17324d;
}

.message-list {
  display: grid;
  gap: 12px;
}

.message-item {
  padding: 18px 20px;
  border-radius: 20px;
  border: 1px solid #dce8f3;
  background: #fff;
}

.message-item.unread {
  border-color: rgba(214, 136, 25, 0.35);
  box-shadow: inset 4px 0 0 #d48819;
}

.message-top {
  display: flex;
  justify-content: space-between;
  gap: 14px;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.message-time {
  color: #7a92aa;
  font-size: 13px;
}

.message-top h3 {
  margin: 0 0 8px;
}

.message-top p {
  margin: 0;
  color: #6d839a;
}

.message-bottom {
  display: flex;
  gap: 16px;
  margin-top: 14px;
  color: #7a92aa;
  font-size: 13px;
}

.empty-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 12px;
}
</style>
