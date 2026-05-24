<template>
  <div class="layout register-layout">
    <section class="register-visual">
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
        <h2>智慧校园 · 学生注册</h2>
        <p>填写个人信息完成账号注册，开始使用智能考勤系统。</p>
      </div>
    </section>

    <section class="register-panel">
      <div class="card register-card">
        <p class="register-kicker">Smart Campus Attendance</p>
        <h1>学生注册</h1>

        <div class="field">
          <label for="realName">真实姓名</label>
          <input
            id="realName"
            v-model="form.realName"
            placeholder="请输入真实姓名"
          />
        </div>

        <div class="field">
          <label for="studentNo">学号</label>
          <input
            id="studentNo"
            v-model="form.studentNo"
            placeholder="请输入学号"
          />
        </div>

        <div class="field">
          <label for="classId">所属班级</label>
          <select id="classId" v-model="form.classId" class="select-input">
            <option value="" disabled>请选择班级</option>
            <option v-for="cls in classes" :key="cls.id" :value="cls.id">
              {{ cls.className }}（{{ cls.classCode }}）
            </option>
          </select>
        </div>

        <div class="field">
          <label for="username">登录账号</label>
          <input
            id="username"
            v-model="form.username"
            autocomplete="username"
            placeholder="请设置登录账号"
          />
        </div>

        <div class="field">
          <label for="password">登录密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            autocomplete="new-password"
            placeholder="请设置密码（不少于6位）"
          />
        </div>

        <div class="field">
          <label for="confirmPassword">确认密码</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            autocomplete="new-password"
            placeholder="请再次输入密码"
          />
        </div>

        <button class="btn-register" :disabled="loading" @click="submit">
          {{ loading ? '注册中...' : '立即注册' }}
        </button>
        <p v-if="error" class="error-text">{{ error }}</p>
        <p v-if="success" class="success-text">注册成功！即将跳转登录页...</p>

        <p class="login-link">
          已有账号？<router-link to="/login">返回登录</router-link>
        </p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { registerApi, getClassesApi } from '../../api/auth';

const form = reactive({
  realName: '',
  studentNo: '',
  classId: '',
  username: '',
  password: '',
  confirmPassword: ''
});
const classes = ref([]);
const error = ref('');
const success = ref(false);
const loading = ref(false);
const router = useRouter();

onMounted(async () => {
  try {
    const res = await getClassesApi();
    classes.value = res;
  } catch (e) {
    classes.value = [];
  }
});

const submit = async () => {
  error.value = '';
  const { realName, studentNo, classId, username, password, confirmPassword } = form;
  if (!realName || !studentNo || !classId || !username || !password || !confirmPassword) {
    error.value = '请填写所有字段';
    return;
  }
  if (password.length < 6) {
    error.value = '密码不能少于6位';
    return;
  }
  if (password !== confirmPassword) {
    error.value = '两次输入密码不一致';
    return;
  }
  loading.value = true;
  try {
    await registerApi({ realName, studentNo, classId, username, password });
    success.value = true;
    setTimeout(() => router.push('/login'), 1500);
  } catch (e) {
    error.value = e.message || '注册失败，请稍后重试';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-layout {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  min-height: 100vh;
  background: linear-gradient(145deg, #eef4fb 0%, #f7fbff 60%, #f1f7f4 100%);
}

.register-visual {
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

.register-panel {
  display: grid;
  place-items: center;
  padding: 24px;
  overflow-y: auto;
}

.register-card {
  width: min(460px, 100%);
  padding: 36px;
  border-radius: 24px;
  margin: auto;
}

.register-kicker {
  margin: 0;
  color: #4a6f93;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  font-size: 0.8rem;
}

h1 {
  margin: 12px 0 22px;
  font-size: clamp(1.8rem, 3vw, 2.4rem);
  line-height: 1.12;
}

.field + .field {
  margin-top: 12px;
}

.field label {
  display: inline-block;
  margin-bottom: 6px;
  color: #35506b;
  font-size: 0.94rem;
  font-weight: 600;
}

.select-input {
  width: 100%;
  padding: 0 14px;
  height: 48px;
  border: 1.5px solid #c8d8e8;
  border-radius: 10px;
  background: #fff;
  font-size: 0.96rem;
  color: #2c3e50;
  cursor: pointer;
  outline: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%234a6f93' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
}

.select-input:focus {
  border-color: #3a7bd5;
  box-shadow: 0 0 0 3px rgba(58, 123, 213, 0.12);
}

.btn-register {
  margin-top: 20px;
  width: 100%;
  min-height: 52px;
  font-size: 1rem;
  font-weight: 700;
}

.btn-register:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.error-text {
  margin: 12px 0 0;
  color: #c63636;
}

.success-text {
  margin: 12px 0 0;
  color: #1a8a4a;
  font-weight: 600;
}

.login-link {
  margin-top: 16px;
  text-align: center;
  color: #5a7a9a;
  font-size: 0.92rem;
}

.login-link a {
  color: #3a7bd5;
  font-weight: 600;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

@keyframes grid-float {
  0% { transform: perspective(480px) rotateX(46deg) translateY(0); }
  50% { transform: perspective(480px) rotateX(46deg) translateY(16px); }
  100% { transform: perspective(480px) rotateX(46deg) translateY(0); }
}

@keyframes lift {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@media (max-width: 980px) {
  .register-layout {
    grid-template-columns: 1fr;
  }

  .register-visual {
    min-height: 40vh;
    padding: 32px 24px;
  }

  .register-card {
    margin-top: -24px;
    width: min(560px, 100%);
  }
}

@media (max-width: 640px) {
  .register-panel {
    padding: 14px;
    align-items: start;
  }

  .register-card {
    padding: 24px 18px;
    border-radius: 18px;
  }
}
</style>
