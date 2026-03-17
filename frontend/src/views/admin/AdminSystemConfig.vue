<template>
  <div class="card">
    <h3 style="margin-top:0;">系统配置</h3>
    <div style="display:flex;gap:8px;margin-bottom:10px;">
      <button @click="loadData">刷新</button>
      <button v-permission="'system:config:update'" @click="save">保存变更</button>
    </div>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>配置键</th>
          <th>配置名</th>
          <th>配置值</th>
          <th>备注</th>
          <th>更新时间</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.configKey">
          <td>{{ item.configKey }}</td>
          <td>{{ item.configName }}</td>
          <td><input v-model="item.configValue" style="min-width:260px;" /></td>
          <td>{{ item.remark }}</td>
          <td>{{ item.updatedAt || '-' }}</td>
        </tr>
      </tbody>
    </table>
    <p v-if="msg">{{ msg }}</p>
    <p v-if="error" style="color:#c63636;">{{ error }}</p>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listSystemConfigApi, updateSystemConfigApi } from '../../api/adminStats';

const list = ref([]);
const msg = ref('');
const error = ref('');

const loadData = async () => {
  msg.value = '';
  error.value = '';
  list.value = await listSystemConfigApi();
};

const save = async () => {
  msg.value = '';
  error.value = '';
  try {
    await updateSystemConfigApi(
      list.value.map((it) => ({
        configKey: it.configKey,
        configValue: it.configValue
      }))
    );
    msg.value = '保存成功';
    await loadData();
  } catch (e) {
    error.value = e.message || '保存失败';
  }
};

onMounted(loadData);
</script>
