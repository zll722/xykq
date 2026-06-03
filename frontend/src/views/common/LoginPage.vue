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
          <div class="password-label-row">
            <label for="password">密码</label>
            <span class="forgot-password-link" @click="showForgotModal = true">忘记密码？</span>
          </div>
          <input
            id="password"
            v-model="form.password"
            data-testid="login-password"
            type="password"
            autocomplete="current-password"
            placeholder="请输入密码"
          />
        </div>

        <button data-testid="login-submit" class="btn-login" @click="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <p v-if="error" class="error-text">{{ error }}</p>
        <p class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </p>
      </div>
    </section>

    <!-- 忘记密码弹窗 -->
    <el-dialog
      v-model="showForgotModal"
      title="重置密码"
      width="440px"
      append-to-body
      @close="closeForgotDialog"
    >
      <el-form :model="forgotForm" :rules="forgotRules" ref="forgotFormRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="forgotForm.username" placeholder="请输入您的登录账号" />
        </el-form-item>

        <el-form-item label="绑定邮箱" prop="email">
          <el-input v-model.trim="forgotForm.email" placeholder="请输入绑定的邮箱" />
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <div class="code-input-wrap">
            <el-input v-model.trim="forgotForm.code" placeholder="请输入6位验证码" />
            <el-button type="primary" :disabled="forgotCountdown > 0 || sendingForgotCode" @click="sendForgotCode">
              {{ forgotCountdown > 0 ? `${forgotCountdown}s后重试` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="forgotForm.newPassword" type="password" show-password placeholder="请输入新密码（最少6位）" />
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="forgotForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeForgotDialog">取消</el-button>
          <el-button type="primary" @click="submitResetPassword" :loading="resettingPassword">重置密码</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { sendResetCodeApi, resetPasswordApi } from '../../api/auth';
import { ElMessage } from 'element-plus';

const form = reactive({ username: '', password: '' });
const error = ref('');
const loading = ref(false);
const router = useRouter();
const authStore = useAuthStore();

// 忘记密码弹窗逻辑
const showForgotModal = ref(false);
const resettingPassword = ref(false);
const sendingForgotCode = ref(false);
const forgotCountdown = ref(0);
const forgotFormRef = ref(null);
let forgotTimer = null;

const forgotForm = reactive({
  username: '',
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
});

const validateConfirmPass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'));
  } else if (value !== forgotForm.newPassword) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const forgotRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPass, trigger: 'blur' }
  ]
};

const sendForgotCode = async () => {
  if (!forgotForm.username) {
    ElMessage.warning('请输入用户名');
    return;
  }
  if (!forgotForm.email) {
    ElMessage.warning('请输入邮箱');
    return;
  }
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailPattern.test(forgotForm.email)) {
    ElMessage.warning('请输入正确的邮箱格式');
    return;
  }

  sendingForgotCode.value = true;
  try {
    await sendResetCodeApi({ username: forgotForm.username, email: forgotForm.email });
    ElMessage.success('验证码已发送，请查收邮箱');
    forgotCountdown.value = 60;
    forgotTimer = setInterval(() => {
      forgotCountdown.value--;
      if (forgotCountdown.value <= 0) {
        clearInterval(forgotTimer);
      }
    }, 1000);
  } catch (e) {
    ElMessage.error(e.message || '发送验证码失败');
  } finally {
    sendingForgotCode.value = false;
  }
};

const closeForgotDialog = () => {
  showForgotModal.value = false;
  if (forgotFormRef.value) forgotFormRef.value.resetFields();
  forgotForm.username = '';
  forgotForm.email = '';
  forgotForm.code = '';
  forgotForm.newPassword = '';
  forgotForm.confirmPassword = '';
  if (forgotTimer) {
    clearInterval(forgotTimer);
    forgotTimer = null;
  }
  forgotCountdown.value = 0;
};

const submitResetPassword = async () => {
  if (!forgotFormRef.value) return;
  const valid = await forgotFormRef.value.validate().catch(() => false);
  if (!valid) return;

  resettingPassword.value = true;
  try {
    await resetPasswordApi({
      username: forgotForm.username,
      email: forgotForm.email,
      code: forgotForm.code,
      newPassword: forgotForm.newPassword
    });
    ElMessage.success('密码重置成功，请使用新密码登录');
    closeForgotDialog();
  } catch (e) {
    ElMessage.error(e.message || '重置密码失败');
  } finally {
    resettingPassword.value = false;
  }
};

const submit = async () => {
  error.value = '';
  loading.value = true;
  try {
    if (!form.username || !form.password) {
      throw new Error('请输入用户名和密码');
    }
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
  } finally {
    loading.value = false;
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

.password-label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-password-link {
  font-size: 0.88rem;
  color: #3a7bd5;
  cursor: pointer;
  font-weight: 600;
  transition: color 0.2s;
}

.forgot-password-link:hover {
  color: #0d2f55;
  text-decoration: underline;
}

.code-input-wrap {
  display: flex;
  gap: 12px;
  width: 100%;
}

.code-input-wrap :deep(.el-input) {
  flex: 1;
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

.register-link {
  margin-top: 16px;
  text-align: center;
  color: #5a7a9a;
  font-size: 0.92rem;
}

.register-link a {
  color: #3a7bd5;
  font-weight: 600;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
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
