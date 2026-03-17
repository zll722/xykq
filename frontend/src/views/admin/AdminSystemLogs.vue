<template>
  <div class="card">
    <h3 style="margin-top:0;">操作日志</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px;">
      <input v-model="moduleName" placeholder="模块名(可空)" />
      <button @click="loadData">查询</button>
    </div>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>操作人</th>
          <th>模块</th>
          <th>方法</th>
          <th>URI</th>
          <th>结果</th>
          <th>时间</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.operatorId || '-' }}</td>
          <td>{{ item.moduleName }}</td>
          <td>{{ item.requestMethod }}</td>
          <td>{{ item.requestUri }}</td>
          <td>{{ item.resultCode }}</td>
          <td>{{ item.operatedAt }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listOperationLogsApi } from '../../api/adminStats';

const moduleName = ref('');
const list = ref([]);

const loadData = async () => {
  list.value = await listOperationLogsApi({ moduleName: moduleName.value });
};

onMounted(loadData);
</script>
