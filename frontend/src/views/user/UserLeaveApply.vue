<template>
  <div class="card">
    <h3>请假申请</h3>
    <div class="toolbar">
      <input data-testid="leave-type" v-model="form.leaveType" placeholder="请假类型（如病假）" />
      <input data-testid="leave-start-time" v-model="form.startTime" placeholder="开始时间（2026-03-13T08:00:00）" />
      <input data-testid="leave-end-time" v-model="form.endTime" placeholder="结束时间（2026-03-13T18:00:00）" />
      <input data-testid="leave-proof-url" v-model="form.proofUrl" placeholder="证明材料路径（上传后自动填入）" />
      <input data-testid="leave-file" type="file" @change="onFileChange" />
      <button data-testid="leave-upload-proof" @click="uploadProof">上传证明</button>
    </div>
    <div class="reason-wrap">
      <textarea data-testid="leave-reason" v-model="form.reason" placeholder="请假原因" rows="4" />
    </div>
    <div class="actions">
      <button data-testid="leave-submit" @click="submit">提交申请</button>
      <button data-testid="leave-reset" @click="reset">重置</button>
    </div>
    <p v-if="msg" class="msg-text">{{ msg }}</p>
    <p v-if="error" class="error-text">{{ error }}</p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { applyLeaveApi } from '../../api/leave';
import { uploadFileApi } from '../../api/file';

const form = reactive({
  leaveType: '病假',
  reason: '',
  startTime: '',
  endTime: '',
  proofUrl: ''
});
const msg = ref('');
const error = ref('');
const selectedFile = ref(null);

const submit = async () => {
  msg.value = '';
  error.value = '';
  try {
    const res = await applyLeaveApi(form);
    msg.value = `提交成功，申请ID：${res.id}`;
  } catch (e) {
    error.value = e.message || '提交失败';
  }
};

const reset = () => {
  form.leaveType = '病假';
  form.reason = '';
  form.startTime = '';
  form.endTime = '';
  form.proofUrl = '';
  msg.value = '';
  error.value = '';
};

const onFileChange = (e) => {
  selectedFile.value = e.target.files?.[0] || null;
};

const uploadProof = async () => {
  if (!selectedFile.value) {
    error.value = '请先选择文件';
    return;
  }
  error.value = '';
  form.proofUrl = await uploadFileApi(selectedFile.value, 'leave');
};
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.toolbar > * {
  min-width: 220px;
  flex: 1 1 220px;
}

.reason-wrap {
  margin-top: 10px;
}

.actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}

.msg-text,
.error-text {
  margin-top: 10px;
}

.error-text {
  color: #c63636;
}
</style>
