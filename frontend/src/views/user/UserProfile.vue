<template>
  <div class="card">
    <h3 style="margin-top:0;">个人资料</h3>
    <div style="display:grid;gap:8px;max-width:520px;">
      <input v-model="form.username" disabled />
      <input v-model="form.realName" placeholder="姓名" />
      <input v-model="form.phone" placeholder="手机号" />
      <input v-model="form.email" placeholder="邮箱" />
      <input v-model="form.avatarUrl" placeholder="头像URL" />
      <input type="file" @change="onFileChange" />
      <button @click="uploadAvatar">上传头像</button>
      <img v-if="form.avatarUrl" :src="form.avatarUrl" alt="avatar" style="width:80px;height:80px;object-fit:cover;border-radius:8px;" />
    </div>
    <div style="margin-top:10px;">
      <button @click="save">保存</button>
    </div>
    <p v-if="msg">{{ msg }}</p>
    <p v-if="error" style="color:#c63636;">{{ error }}</p>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { getMyProfileApi, updateMyProfileApi } from '../../api/user';
import { uploadFileApi } from '../../api/file';

const form = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  avatarUrl: ''
});
const msg = ref('');
const error = ref('');
const selectedFile = ref(null);

const loadData = async () => {
  const data = await getMyProfileApi();
  form.username = data.username || '';
  form.realName = data.realName || '';
  form.phone = data.phone || '';
  form.email = data.email || '';
  form.avatarUrl = data.avatarUrl || '';
};

const save = async () => {
  msg.value = '';
  error.value = '';
  try {
    await updateMyProfileApi({
      realName: form.realName,
      phone: form.phone,
      email: form.email,
      avatarUrl: form.avatarUrl
    });
    msg.value = '保存成功';
  } catch (e) {
    error.value = e.message || '保存失败';
  }
};

const onFileChange = (e) => {
  selectedFile.value = e.target.files?.[0] || null;
};

const uploadAvatar = async () => {
  if (!selectedFile.value) {
    error.value = '请先选择文件';
    return;
  }
  error.value = '';
  form.avatarUrl = await uploadFileApi(selectedFile.value, 'avatar');
};

onMounted(loadData);
</script>
