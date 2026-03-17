<template>
  <section class="users-page">
    <div class="crumb">首页 <span>/</span> 用户管理</div>
    <h1>用户管理</h1>

    <div class="card users-card">
      <div class="toolbar">
        <input
          v-model.trim="filters.keyword"
          data-testid="users-search"
          class="search-input"
          placeholder="搜索用户名或姓名"
          @keyup.enter="loadData"
        />
        <select v-model="filters.roleCode" data-testid="users-role-filter">
          <option value="">全部角色</option>
          <option value="USER">USER</option>
          <option value="ADMIN">ADMIN</option>
        </select>
        <select v-model="filters.status" data-testid="users-status-filter">
          <option value="">全部状态</option>
          <option value="1">启用</option>
          <option value="0">禁用</option>
        </select>
        <button data-testid="users-create" v-permission="'user:create'" @click="openCreate">新建</button>
        <button data-testid="users-refresh" class="btn-secondary" @click="loadData">刷新</button>
      </div>

      <div v-if="editorVisible" class="editor-panel">
        <h3>{{ formMode === 'create' ? '新建用户' : `编辑用户 #${form.id}` }}</h3>
        <div class="editor-grid">
          <input
            v-model.trim="form.username"
            :disabled="formMode === 'edit'"
            placeholder="用户名"
          />
          <input
            v-model.trim="form.password"
            :placeholder="formMode === 'create' ? '密码' : '密码（编辑时可留空）'"
            type="password"
          />
          <input v-model.trim="form.realName" placeholder="姓名" />
          <select v-model="form.roleCode">
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          <select v-model.number="form.status">
            <option :value="1">启用</option>
            <option :value="0">禁用</option>
          </select>
        </div>
        <div class="editor-actions">
          <button
            v-if="formMode === 'create'"
            data-testid="users-create-submit"
            v-permission="'user:create'"
            @click="submitEditor"
          >
            创建
          </button>
          <button
            v-else
            data-testid="users-edit-submit"
            v-permission="'user:update'"
            @click="submitEditor"
          >
            保存
          </button>
          <button class="btn-secondary" @click="closeEditor">取消</button>
        </div>
      </div>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>姓名</th>
            <th>角色</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.username }}</td>
            <td>{{ item.realName }}</td>
            <td>
              <span class="tag" :class="item.roleCode === 'ADMIN' ? 'tag-admin' : 'tag-user'">
                {{ item.roleCode }}
              </span>
            </td>
            <td>
              <span class="tag" :class="item.status === 1 ? 'tag-on' : 'tag-off'">
                {{ item.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>
              <div class="ops">
                <button
                  v-permission="'user:update'"
                  :data-testid="`users-edit-${item.id}`"
                  class="icon-btn"
                  title="编辑"
                  @click="openEdit(item)"
                >
                  <span class="icon">✎</span>
                </button>
                <button
                  v-permission="'user:status'"
                  :data-testid="`users-status-${item.id}`"
                  class="icon-btn warn"
                  :title="item.status === 1 ? '禁用' : '启用'"
                  @click="setStatus(item.id, item.status === 1 ? 0 : 1)"
                >
                  <span class="icon">⦸</span>
                </button>
                <button
                  v-permission="'user:delete'"
                  :data-testid="`users-delete-${item.id}`"
                  class="icon-btn danger"
                  title="删除"
                  @click="remove(item.id)"
                >
                  <span class="icon">×</span>
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="!list.length">
            <td colspan="6" class="empty">暂无用户数据</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import {
  createAdminUserApi,
  deleteAdminUserApi,
  listAdminUsersApi,
  updateAdminUserApi,
  updateAdminUserStatusApi
} from '../../api/adminUsers';

const list = ref([]);
const filters = reactive({
  keyword: '',
  roleCode: '',
  status: ''
});

const editorVisible = ref(false);
const formMode = ref('create');
const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  roleCode: 'USER',
  status: 1
});

const loadData = async () => {
  const params = {
    keyword: filters.keyword || undefined,
    roleCode: filters.roleCode || undefined,
    status: filters.status === '' ? undefined : Number(filters.status)
  };
  list.value = await listAdminUsersApi(params);
};

const resetForm = () => {
  form.id = null;
  form.username = '';
  form.password = '';
  form.realName = '';
  form.roleCode = 'USER';
  form.status = 1;
};

const openCreate = () => {
  formMode.value = 'create';
  resetForm();
  editorVisible.value = true;
};

const openEdit = (item) => {
  formMode.value = 'edit';
  form.id = item.id;
  form.username = item.username;
  form.password = '';
  form.realName = item.realName;
  form.roleCode = item.roleCode;
  form.status = item.status;
  editorVisible.value = true;
};

const closeEditor = () => {
  editorVisible.value = false;
  resetForm();
};

const submitEditor = async () => {
  const payload = {
    username: form.username,
    password: form.password,
    realName: form.realName,
    roleCode: form.roleCode,
    status: form.status
  };

  if (formMode.value === 'create') {
    await createAdminUserApi(payload);
  } else {
    await updateAdminUserApi(form.id, payload);
  }

  closeEditor();
  await loadData();
};

const setStatus = async (id, status) => {
  await updateAdminUserStatusApi(id, status);
  await loadData();
};

const remove = async (id) => {
  if (!window.confirm('确认删除该用户吗？')) {
    return;
  }
  await deleteAdminUserApi(id);
  await loadData();
};

onMounted(loadData);
</script>

<style scoped>
.users-page {
  display: grid;
  gap: 10px;
}

.crumb {
  color: #67829f;
  font-size: 0.9rem;
}

.crumb span {
  margin: 0 6px;
  color: #9ab0c4;
}

h1 {
  margin: 0;
  font-size: 1.8rem;
  line-height: 1.2;
}

.users-card {
  padding: 16px;
  border-radius: 16px;
}

.toolbar {
  display: grid;
  grid-template-columns: minmax(220px, 1.4fr) repeat(2, minmax(140px, 0.8fr)) auto auto;
  gap: 10px;
  align-items: center;
  margin-bottom: 14px;
}

.search-input {
  min-width: 0;
}

.btn-secondary {
  background: linear-gradient(130deg, #8297b1 0%, #6f87a5 100%);
}

.editor-panel {
  margin-bottom: 14px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #d8e3ef;
  background: #f7fbff;
}

.editor-panel h3 {
  margin: 0 0 10px;
  font-size: 1rem;
}

.editor-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.editor-actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}

table {
  border-radius: 14px;
}

tbody tr {
  transition: background-color 0.18s ease;
}

tbody tr:hover {
  background: #f4f9ff;
}

.tag {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 0.82rem;
  font-weight: 700;
}

.tag-user {
  color: #1f5eaa;
  background: #e7f1ff;
}

.tag-admin {
  color: #b86412;
  background: #fff0df;
}

.tag-on {
  color: #1f8b56;
  background: #e5f7ee;
}

.tag-off {
  color: #7a8494;
  background: #eef1f5;
}

.ops {
  display: flex;
  align-items: center;
  gap: 6px;
}

.icon-btn {
  min-height: 30px;
  width: 30px;
  padding: 0;
  display: grid;
  place-items: center;
  border-radius: 9px;
}

.icon-btn.warn {
  background: linear-gradient(130deg, #4e86d9 0%, #3c6eaf 100%);
}

.icon-btn.danger {
  background: linear-gradient(130deg, #cf5a5a 0%, #b44343 100%);
}

.icon {
  font-size: 0.9rem;
  line-height: 1;
}

.empty {
  text-align: center;
  color: #7e93a8;
}

@media (max-width: 1160px) {
  .toolbar {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .editor-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 680px) {
  .toolbar,
  .editor-grid {
    grid-template-columns: 1fr;
  }
}
</style>
