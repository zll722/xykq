<template>
  <div class="layout login-layout">
    <section class="login-visual">
      <div class="visual-grid" />
      <div class="visual-glow visual-glow-top" />
      <div class="visual-glow visual-glow-bottom" />
      <div class="visual-card">
        <div class="visual-avatar">
          <span />
        </div>
        <div class="visual-lines">
          <i />
          <i />
          <i />
        </div>
      </div>
      <div class="visual-copy">
        <h2>智慧校园 · 智能考勤</h2>
        <p>融合课程、签到、请假与统计能力，让校园管理更高效、更智能。</p>
      </div>
    </section>

    <section class="login-panel">
      <div class="card login-card">
        <p class="login-kicker">Smart Campus Attendance</p>
        <h1>智能校园考勤系统</h1>

        <div class="field">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="form.username"
            data-testid="login-username"
            autocomplete="username"
            placeholder="请输入用户名"
          />
        </div>

        <div class="field">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            data-testid="login-password"
            type="password"
            autocomplete="current-password"
            placeholder="请输入密码"
          />
        </div>

        <button data-testid="login-submit" class="btn-login" @click="submit">登录</button>
        <p v-if="error" class="error-text">{{ error }}</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const form = reactive({ username: '', password: '' });
const error = ref('');
const router = useRouter();
const authStore = useAuthStore();

const submit = async () => {
  error.value = '';
  try {
    await authStore.login(form.username, form.password);
    const roleCode = authStore.userInfo?.roleCode;
    if (roleCode === 'ADMIN') {
      router.push('/admin/dashboard');
    } else if (roleCode === 'TEACHER') {
      router.push('/teacher/dashboard');
    } else if (roleCode === 'COUNSELOR') {
      router.push('/counselor/dashboard');
    } else {
      router.push('/user/dashboard');
    }
  } catch (e) {
    error.value = e.message || '登录失败';
  }
};
</script>

<style scoped>
.login-layout {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  min-height: 100vh;
  background: linear-gradient(145deg, #eef4fb 0%, #f7fbff 60%, #f1f7f4 100%);
}

.login-visual {
  position: relative;
  overflow: hidden;
  padding: 48px;
  color: #ffffff;
  background: linear-gradient(155deg, #0d2f55 0%, #1d5b8f 50%, #21928c 100%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  isolation: isolate;
}

.visual-grid {
  position: absolute;
  inset: -20% -10% auto -10%;
  height: 80%;
  opacity: 0.3;
  background-image: linear-gradient(rgba(255, 255, 255, 0.24) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.24) 1px, transparent 1px);
  background-size: 28px 28px;
  transform: perspective(480px) rotateX(46deg);
  transform-origin: top;
  animation: grid-float 14s linear infinite;
}

.visual-glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(12px);
  z-index: -1;
}

.visual-glow-top {
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(98, 222, 208, 0.72), rgba(98, 222, 208, 0));
  top: -110px;
  right: -90px;
}

.visual-glow-bottom {
  width: 420px;
  height: 420px;
  background: radial-gradient(circle, rgba(167, 219, 255, 0.56), rgba(167, 219, 255, 0));
  bottom: -180px;
  left: -120px;
}

.visual-card {
  width: 290px;
  height: 175px;
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 24px;
  padding: 18px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.12);
  animation: lift 3.8s ease-in-out infinite;
}

.visual-avatar {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.22);
}

.visual-avatar span {
  display: block;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid #ffffff;
  position: relative;
}

.visual-avatar span::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #ffffff;
  transform: translate(-50%, -50%);
}

.visual-lines {
  margin-top: 18px;
  display: grid;
  gap: 8px;
}

.visual-lines i {
  display: block;
  height: 7px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
}

.visual-lines i:nth-child(2) {
  width: 78%;
}

.visual-lines i:nth-child(3) {
  width: 62%;
}

.visual-copy {
  margin-top: 22px;
  max-width: 520px;
}

.visual-copy h2 {
  margin: 0 0 10px;
  font-size: 2rem;
  line-height: 1.15;
}

.visual-copy p {
  margin: 0;
  color: rgba(255, 255, 255, 0.88);
}

.login-panel {
  display: grid;
  place-items: center;
  padding: 24px;
}

.login-card {
  width: min(460px, 100%);
  padding: 36px;
  border-radius: 24px;
}

.login-kicker {
  margin: 0;
  color: #4a6f93;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  font-size: 0.8rem;
}

h1 {
  margin: 12px 0 26px;
  font-size: clamp(2rem, 3vw, 2.55rem);
  line-height: 1.12;
}

.field + .field {
  margin-top: 14px;
}

.field label {
  display: inline-block;
  margin-bottom: 8px;
  color: #35506b;
  font-size: 0.94rem;
  font-weight: 600;
}

.btn-login {
  margin-top: 22px;
  width: 100%;
  min-height: 52px;
  font-size: 1rem;
  font-weight: 700;
}

.error-text {
  margin: 14px 0 0;
  color: #c63636;
}

@keyframes grid-float {
  0% {
    transform: perspective(480px) rotateX(46deg) translateY(0);
  }
  50% {
    transform: perspective(480px) rotateX(46deg) translateY(16px);
  }
  100% {
    transform: perspective(480px) rotateX(46deg) translateY(0);
  }
}

@keyframes lift {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@media (max-width: 980px) {
  .login-layout {
    grid-template-columns: 1fr;
  }

  .login-visual {
    min-height: 44vh;
    padding: 32px 24px;
  }

  .login-card {
    margin-top: -24px;
    width: min(560px, 100%);
  }
}

@media (max-width: 640px) {
  .login-panel {
    padding: 14px;
    align-items: start;
  }

  .login-card {
    padding: 24px 18px;
    border-radius: 18px;
  }
}
</style>
