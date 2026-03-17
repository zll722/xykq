<template>
  <div class="card">
    <h3 style="margin-top:0;">消息通知</h3>
    <div style="display:flex;gap:8px;margin-bottom:10px;">
      <button @click="loadData">刷新</button>
      <button @click="readAll">全部已读</button>
    </div>
    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>标题</th>
          <th>内容</th>
          <th>类型</th>
          <th>已读</th>
          <th>时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.title }}</td>
          <td>{{ item.content }}</td>
          <td>{{ item.msgType }}</td>
          <td>{{ item.readFlag === 1 ? '是' : '否' }}</td>
          <td>{{ item.sentAt }}</td>
          <td>
            <button v-if="item.readFlag !== 1" @click="readOne(item.id)">标记已读</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { listMessagesApi, readAllMessageApi, readMessageApi } from '../../api/message';

const list = ref([]);

const loadData = async () => {
  list.value = await listMessagesApi();
};

const readOne = async (id) => {
  await readMessageApi(id);
  await loadData();
};

const readAll = async () => {
  await readAllMessageApi();
  await loadData();
};

onMounted(loadData);
</script>
