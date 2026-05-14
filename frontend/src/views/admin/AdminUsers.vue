<template>
  <div class="users-page">
    <div class="crumb">首页 <span>/</span> 用户管理</div>
    <h1 style="margin-bottom: 20px;">用户管理</h1>

    <el-card class="users-card" shadow="hover">
      <div class="toolbar">
        <el-input
          v-model.trim="filters.keyword"
          data-testid="users-search"
          placeholder="搜索用户名或姓名"
          clearable
          @keyup.enter="loadData"
          style="width: 200px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select v-model="filters.roleCode" data-testid="users-role-filter" placeholder="全部角色" clearable style="width: 160px">
          <el-option label="普通用户(USER)" value="USER" />
          <el-option label="管理员(ADMIN)" value="ADMIN" />
          <el-option label="辅导员(COUNSELOR)" value="COUNSELOR" />
          <el-option label="授课教师(TEACHER)" value="TEACHER" />
        </el-select>

        <el-select v-model="filters.status" data-testid="users-status-filter" placeholder="全部状态" clearable style="width: 150px">
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>

        <el-button type="primary" @click="loadData" icon="Search">搜索</el-button>
        <el-button data-testid="users-create" v-permission="'user:create'" type="success" icon="Plus" @click="openCreate">新建</el-button>
      </div>

      <el-table :data="list" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column label="班级" min-width="180">
          <template #default="{ row }">
            <span>{{ getClassName(row.classId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="roleCode" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.roleCode === 'ADMIN' ? 'danger' : row.roleCode === 'TEACHER' ? 'success' : row.roleCode === 'COUNSELOR' ? 'warning' : 'info'">
              {{ { ADMIN: '管理员', TEACHER: '授课教师', COUNSELOR: '辅导员', USER: '学生' }[row.roleCode] || row.roleCode }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => setStatus(row.id, val)"
              v-permission="'user:status'"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'user:update'"
              link
              type="primary"
              icon="Edit"
              @click="openEdit(row)"
            >编辑</el-button>
            <el-popconfirm
              title="确认删除该用户吗？"
              @confirm="remove(row.id)"
            >
              <template #reference>
                <el-button
                  v-permission="'user:delete'"
                  link
                  type="danger"
                  icon="Delete"
                >删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无用户数据" />
        </template>
      </el-table>
    </el-card>

    <el-dialog
      v-model="editorVisible"
      :title="formMode === 'create' ? '新建用户' : `编辑用户 #${form.id}`"
      width="500px"
      @close="closeEditor"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" :disabled="formMode === 'edit'" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" :prop="formMode === 'create' ? 'password' : ''">
          <el-input
            v-model.trim="form.password"
            type="password"
            show-password
            :placeholder="formMode === 'create' ? '请输入密码' : '不修改请留空'"
          />
        </el-form-item>

        <el-form-item label="姓名" prop="realName">
          <el-input v-model.trim="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="角色" prop="roleCode">
          <el-radio-group v-model="form.roleCode" @change="handleRoleChange">
            <el-radio value="USER">学生</el-radio>
            <el-radio value="COUNSELOR">辅导员</el-radio>
            <el-radio value="TEACHER">授课教师</el-radio>
            <el-radio value="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="form.roleCode === 'USER'" label="班级" prop="classId">
          <el-select
            v-model="form.classId"
            value-key="id"
            placeholder="请选择班级"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.className"
              :value="item"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEditor">取消</el-button>
          <el-button type="primary" @click="submitEditor" :loading="submitting">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  createAdminUserApi,
  deleteAdminUserApi,
  listAdminUserClassesApi,
  listAdminUsersApi,
  updateAdminUserApi,
  updateAdminUserStatusApi
} from '../../api/adminUsers';

const list = ref([]);
const loading = ref(false);
const submitting = ref(false);
const formRef = ref(null);
const classOptions = ref([]);

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
  classId: null,
  status: 1
});

const rules = computed(() => ({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: formMode.value === 'create' ? [{ required: true, message: '请输入密码', trigger: 'blur' }] : [],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  classId: form.roleCode === 'USER' ? [{ required: true, message: '请选择班级', trigger: 'change' }] : []
}));

const normalizeClassOption = (item) => {
  if (!item || item.id == null) return null;
  return {
    ...item,
    id: Number(item.id)
  };
};

const getClassName = (classId) => {
  if (!classId) return '-';
  const normalizedId = Number(classId);
  return classOptions.value.find((item) => item.id === normalizedId)?.className || `班级#${normalizedId}`;
};

const loadClassOptions = async () => {
  try {
    const data = await listAdminUserClassesApi();
    classOptions.value = Array.isArray(data) ? data.map(normalizeClassOption).filter(Boolean) : [];
  } catch (error) {
    classOptions.value = [];
    ElMessage.error(error.message || '班级加载失败');
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      keyword: filters.keyword || undefined,
      roleCode: filters.roleCode || undefined,
      status: filters.status === '' ? undefined : Number(filters.status)
    };
    const data = await listAdminUsersApi(params);
    list.value = Array.isArray(data)
      ? data.map((item) => ({
          ...item,
          classId: item.classId == null ? null : Number(item.classId)
        }))
      : [];
  } catch (error) {
    ElMessage.error(error.message || '加载失败');
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  form.id = null;
  form.username = '';
  form.password = '';
  form.realName = '';
  form.roleCode = 'USER';
  form.classId = null;
  form.status = 1;
};

const handleRoleChange = (roleCode) => {
  if (roleCode !== 'USER') {
    form.classId = null;
  }
};

const openCreate = async () => {
  formMode.value = 'create';
  resetForm();
  await loadClassOptions();
  editorVisible.value = true;
};

const openEdit = async (item) => {
  formMode.value = 'edit';
  resetForm();
  await loadClassOptions();
  form.id = item.id;
  form.username = item.username;
  form.password = '';
  form.realName = item.realName;
  form.roleCode = item.roleCode;
  form.classId = form.roleCode === 'USER'
    ? classOptions.value.find((option) => option.id === Number(item.classId)) || null
    : null;
  form.status = item.status;
  editorVisible.value = true;
};

const closeEditor = () => {
  editorVisible.value = false;
};

const submitEditor = async () => {
  if (!formRef.value) return;
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) {
    return;
  }
  submitting.value = true;
  try {
    const payload = {
      username: form.username,
      realName: form.realName,
      roleCode: form.roleCode,
      status: form.status,
      classId: form.roleCode === 'USER' ? form.classId?.id ?? null : null
    };

    if (form.password) {
      payload.password = form.password;
    }

    if (formMode.value === 'create') {
      await createAdminUserApi(payload);
      ElMessage.success('创建成功');
    } else {
      await updateAdminUserApi(form.id, payload);
      ElMessage.success('更新成功');
    }

    closeEditor();
    loadData();
  } catch (error) {
    ElMessage.error(error.message || '操作失败');
  } finally {
    submitting.value = false;
  }
};

const setStatus = async (id, status) => {
  try {
    await updateAdminUserStatusApi(id, status);
    ElMessage.success(`用户已${status === 1 ? '启用' : '禁用'}`);
  } catch (error) {
    ElMessage.error(error.message || '状态更新失败');
    loadData();
  }
};

const remove = async (id) => {
  try {
    await deleteAdminUserApi(id);
    ElMessage.success('删除成功');
    loadData();
  } catch (error) {
    ElMessage.error(error.message || '删除失败');
  }
};

onMounted(async () => {
  await loadClassOptions();
  await loadData();
});
</script>

<style scoped>
.crumb {
  color: #67829f;
  font-size: 0.9rem;
  margin-bottom: 10px;
}
.crumb span {
  margin: 0 6px;
  color: #9ab0c4;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
</style>
