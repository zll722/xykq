<template>
  <div class="card">
    <h3 style="margin-top:0;">修改密码</h3>
    <div style="display:grid;gap:8px;max-width:400px;">
      <input v-model="form.oldPassword" type="password" placeholder="旧密码" />
      <input v-model="form.newPassword" type="password" placeholder="新密码" />
    </div>
    <div style="margin-top:10px;">
      <button @click="submit">修改</button>
    </div>
    <p v-if="msg">{{ msg }}</p>
    <p v-if="error" style="color:#c63636;">{{ error }}</p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { changePasswordApi } from '../../api/user';

const form = reactive({
  oldPassword: '',
  newPassword: ''
});
const msg = ref('');
const error = ref('');

const submit = async () => {
  msg.value = '';
  error.value = '';
  try {
    await changePasswordApi(form);
    msg.value = '密码修改成功，请下次使用新密码登录';
    form.oldPassword = '';
    form.newPassword = '';
  } catch (e) {
    error.value = e.message || '修改失败';
  }
};
</script>
