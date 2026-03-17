<template>
  <section class="user-dashboard">
    <div class="crumb">首页 <span>/</span> 用户首页</div>
    <h1>用户首页</h1>

    <div class="stats-grid">
      <article class="card stat-card">
        <p>本周签到率</p>
        <strong>96%</strong>
      </article>
      <article class="card stat-card">
        <p>待处理请假</p>
        <strong>1</strong>
      </article>
      <article class="card stat-card">
        <p>本月课程数</p>
        <strong>28</strong>
      </article>
      <article class="card stat-card">
        <p>未读消息</p>
        <strong>0</strong>
      </article>
    </div>

    <div class="card quick-card">
      <h3>快捷入口</h3>
      <div class="quick-links">
        <RouterLink to="/user/sign-in">在线签到</RouterLink>
        <RouterLink to="/user/leave-apply">请假申请</RouterLink>
        <RouterLink to="/user/attendance-records">我的考勤</RouterLink>
        <RouterLink to="/user/messages">消息通知</RouterLink>
      </div>
    </div>
  </section>
</template>

<style scoped>
.user-dashboard {
  display: grid;
  gap: 12px;
}

.crumb {
  color: #67829f;
  font-size: 0.9rem;
}

.crumb span {
  margin: 0 6px;
  color: #9ab0c4;
}

h1 {
  margin: 0;
  font-size: 1.8rem;
  line-height: 1.2;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.stat-card {
  padding: 14px;
  border-radius: 14px;
}

.stat-card p {
  color: #5f7a94;
}

.stat-card strong {
  margin-top: 6px;
  display: inline-block;
  font-size: 1.6rem;
  line-height: 1;
  color: #1f496f;
}

.quick-card {
  border-radius: 14px;
}

.quick-card h3 {
  margin-bottom: 10px;
}

.quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-links a {
  text-decoration: none;
  color: #1f4f82;
  background: linear-gradient(150deg, #edf4ff 0%, #e8f2ff 68%, #ebf8f1 100%);
  border: 1px solid #cce0f5;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 0.92rem;
}

@media (max-width: 980px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
