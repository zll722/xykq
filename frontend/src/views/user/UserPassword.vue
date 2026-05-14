<template>
  <div class="password-page">
    <section class="password-hero">
      <div>
        <p class="hero-tag">账号安全</p>
        <h1>修改密码时给足确认和提示</h1>
        <p>至少保证你知道旧密码、新密码和确认密码分别是什么，避免输错后还不自知。</p>
      </div>
    </section>

    <el-row :gutter="18">
      <el-col :xs="24" :xl="16">
        <el-card class="password-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div>
                <span class="title">修改密码</span>
                <p>修改成功后，建议下次登录时使用新密码验证</p>
              </div>
            </div>
          </template>

          <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="password-form">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input v-model="form.oldPassword" type="password" show-password name="oldPassword" placeholder="请输入当前密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="form.newPassword" type="password" show-password name="newPassword" placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" show-password name="confirmPassword" placeholder="请再次输入新密码" />
            </el-form-item>
            <div class="form-actions">
              <el-button type="primary" :loading="loading" @click="submit">确认修改</el-button>
            </div>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="8">
        <el-card class="tip-card" shadow="hover">
          <template #header>
            <div class="card-header compact">
              <div>
                <span class="title">安全提醒</span>
                <p>降低输错和遗忘风险</p>
              </div>
            </div>
          </template>
          <ul class="tip-list">
            <li>新密码尽量不要和旧密码完全一致</li>
            <li>修改成功后建议退出后重新登录一次</li>
            <li>如果提示旧密码错误，请确认输入法和大小写状态</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { changePasswordApi } from '../../api/user';

const formRef = ref();
const loading = ref(false);
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const rules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入的新密码不一致'));
          return;
        }
        callback();
      },
      trigger: 'blur'
    }
  ]
};

const reset = () => {
  form.oldPassword = '';
  form.newPassword = '';
  form.confirmPassword = '';
  formRef.value?.clearValidate();
};

const submit = async () => {
  await formRef.value.validate();
  loading.value = true;
  try {
    await changePasswordApi({ oldPassword: form.oldPassword, newPassword: form.newPassword });
    ElMessage.success('密码修改成功，请下次使用新密码登录');
    reset();
  } catch (error) {
    ElMessage.error(error.message || '修改失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.password-page {
  display: grid;
  gap: 18px;
}

.password-hero {
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #143a63 0%, #275f9d 55%, #4ca59f 100%);
  color: #fff;
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.password-hero h1 {
  margin: 0;
  color: #fff;
}

.password-hero p {
  max-width: 580px;
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.password-card,
.tip-card {
  border-radius: 24px;
  border: none;
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

.password-form {
  display: grid;
  gap: 8px;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 10px;
}

.tip-list {
  margin: 0;
  padding-left: 18px;
  display: grid;
  gap: 10px;
  color: #5f7893;
}
</style>
