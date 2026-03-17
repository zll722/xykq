<template>
  <div class="card">
    <h3>在线签到</h3>
    <div class="toolbar">
      <select data-testid="sign-in-schedule" v-model="scheduleId">
        <option value="">请选择课程排班</option>
        <option v-for="item in schedules" :key="item.scheduleId" :value="item.scheduleId">
          {{ item.scheduleId }} - {{ item.courseName }} (周{{ item.weekDay }} {{ item.startTime }})
        </option>
      </select>
      <button data-testid="sign-in-submit" @click="submitSignIn">立即签到</button>
      <button data-testid="sign-in-refresh" @click="loadSchedules">刷新课程</button>
    </div>
    <p v-if="result" class="result-text">签到结果：{{ result.status }}，时间：{{ result.signedAt }}</p>
    <p v-if="error" class="error-text">{{ error }}</p>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listMySchedulesApi, signInApi } from '../../api/attendance';

const schedules = ref([]);
const scheduleId = ref('');
const result = ref(null);
const error = ref('');

const loadSchedules = async () => {
  schedules.value = await listMySchedulesApi();
};

const submitSignIn = async () => {
  result.value = null;
  error.value = '';
  if (!scheduleId.value) {
    error.value = '请选择课程排班';
    return;
  }
  try {
    result.value = await signInApi({ scheduleId: Number(scheduleId.value) });
  } catch (e) {
    error.value = e.message || '签到失败';
  }
};

onMounted(loadSchedules);
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.toolbar select {
  min-width: 240px;
}

.result-text,
.error-text {
  margin-top: 12px;
}

.error-text {
  color: #c63636;
}
</style>
