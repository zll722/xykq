<template>
  <div class="card">
    <h3 style="margin-top:0;">考勤补录/修正</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;align-items:center;margin-bottom:10px;">
      <input v-model.number="form.recordId" type="number" placeholder="考勤记录ID" />
      <input v-model="form.afterStatus" placeholder="目标状态(PRESENT/LATE/ABSENT/LEAVE)" style="min-width:240px;" />
      <input v-model="form.reason" placeholder="补录原因" style="min-width:280px;" />
      <button v-permission="'attendance:adjust'" @click="submit">提交</button>
    </div>
    <p v-if="msg">{{ msg }}</p>
    <p v-if="error" style="color:#c63636;">{{ error }}</p>
    <div style="font-size:12px;color:#666;margin-top:10px;">
      提示：先在“考勤记录”页面查看记录ID，再在此页面补录或修正状态。
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { adjustAttendanceRecordApi } from '../../api/adminAttendanceManage';

const form = reactive({
  recordId: '',
  afterStatus: 'PRESENT',
  reason: ''
});
const msg = ref('');
const error = ref('');

const submit = async () => {
  msg.value = '';
  error.value = '';
  if (!form.recordId || !form.afterStatus || !form.reason) {
    error.value = '请完整填写记录ID、目标状态和原因';
    return;
  }
  try {
    await adjustAttendanceRecordApi(form.recordId, {
      afterStatus: form.afterStatus,
      reason: form.reason
    });
    msg.value = `记录#${form.recordId} 已调整为 ${form.afterStatus}`;
  } catch (e) {
    error.value = e.message || '提交失败';
  }
};
</script>
