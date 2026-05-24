<template>
  <div class="classes-page">
    <div class="crumb">首页 <span>/</span> 班级管理</div>
    <h1 style="margin-bottom: 20px;">班级管理</h1>

    <el-card class="box-card" shadow="hover">
      <div class="toolbar">
        <el-button v-permission="'class:create'" type="success" icon="Plus" @click="openCreate">新增班级</el-button>
        <el-button @click="loadData" icon="Refresh">刷新</el-button>
      </div>

      <el-table :data="list" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="classCode" label="班级编码" />
        <el-table-column prop="className" label="班级名称" />
        <el-table-column label="班主任（辅导员）" min-width="140">
          <template #default="{ row }">
            <span>{{ getCounselorName(row.headTeacherId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量/人数" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'class:update'"
              link
              type="primary"
              icon="Edit"
              @click="openEdit(row)"
            >编辑</el-button>
            <el-popconfirm
              title="确认删除该班级吗？"
              @confirm="remove(row.id)"
            >
              <template #reference>
                <el-button
                  v-permission="'class:delete'"
                  link
                  type="danger"
                  icon="Delete"
                >删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无班级数据" />
        </template>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="formMode === 'create' ? '新增班级' : '编辑班级'"
      width="440px"
      @close="closeDialog"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="班级编码" prop="classCode">
          <el-input v-model.trim="form.classCode" :disabled="formMode === 'edit'" placeholder="如：CS202601" />
        </el-form-item>

        <el-form-item label="班级名称" prop="className">
          <el-input v-model.trim="form.className" placeholder="如：计算机科学26级1班" />
        </el-form-item>

        <el-form-item label="班主任" prop="headTeacherId">
          <el-select
            v-model="form.headTeacherId"
            placeholder="请选择辅导员（可不填）"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in counselorOptions"
              :key="item.id"
              :label="getCounselorLabel(item)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="班级人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="200" style="width: 100%" />
        </el-form-item>

        <el-form-item v-if="formMode === 'edit'" label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  createClassApi,
  deleteClassApi,
  listClassesApi,
  listUsersByRoleApi,
  updateClassApi
} from '../../api/adminTeaching';

const list = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const formMode = ref('create');
const formRef = ref(null);
const counselorOptions = ref([]);

const form = reactive({
  id: null,
  classCode: '',
  className: '',
  headTeacherId: null,
  capacity: 30,
  status: 1
});

const rules = {
  classCode: [{ required: true, message: '请输入班级编码', trigger: 'blur' }],
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
};

const getCounselorName = (headTeacherId) => {
  if (!headTeacherId) return '-';
  const found = counselorOptions.value.find((item) => item.id === Number(headTeacherId));
  return found ? `${found.realName}（${found.username}）` : `ID: ${headTeacherId}`;
};

/**
 * 生成辅导员下拉选项标签，附带已管理的班级信息（排除当前编辑中的班级）
 */
const getCounselorLabel = (counselor) => {
  const managedClasses = list.value.filter(
    (cls) => Number(cls.headTeacherId) === counselor.id && cls.id !== form.id
  );
  const base = `${counselor.realName}（${counselor.username}）`;
  if (managedClasses.length === 0) return base;
  return `${base} — 已管: ${managedClasses.map((c) => c.className).join('、')}`;
};

const loadCounselors = async () => {
  try {
    const data = await listUsersByRoleApi('COUNSELOR');
    counselorOptions.value = Array.isArray(data) ? data : [];
  } catch {
    counselorOptions.value = [];
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    list.value = await listClassesApi();
  } catch (error) {
    ElMessage.error(error.message || '加载失败');
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields();
  form.id = null;
  form.classCode = '';
  form.className = '';
  form.headTeacherId = null;
  form.capacity = 30;
  form.status = 1;
};

const openCreate = async () => {
  formMode.value = 'create';
  resetForm();
  await loadCounselors();
  dialogVisible.value = true;
};

const openEdit = async (row) => {
  formMode.value = 'edit';
  resetForm();
  await loadCounselors();
  form.id = row.id;
  form.classCode = row.classCode;
  form.className = row.className;
  form.headTeacherId = row.headTeacherId ? Number(row.headTeacherId) : null;
  form.capacity = row.capacity;
  form.status = row.status;
  dialogVisible.value = true;
};

const closeDialog = () => {
  dialogVisible.value = false;
};

const submitForm = async () => {
  if (!formRef.value) return;
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const payload = {
      classCode: form.classCode,
      className: form.className,
      headTeacherId: form.headTeacherId || null,
      capacity: form.capacity,
      status: form.status
    };

    if (formMode.value === 'create') {
      await createClassApi(payload);
      ElMessage.success('新增班级成功');
    } else {
      await updateClassApi(form.id, payload);
      ElMessage.success('更新成功');
    }
    closeDialog();
    loadData();
  } catch (error) {
    ElMessage.error(error.message || '操作失败');
  } finally {
    submitting.value = false;
  }
};

const remove = async (id) => {
  try {
    await deleteClassApi(id);
    ElMessage.success('删除成功');
    loadData();
  } catch (error) {
    ElMessage.error(error.message || '删除失败');
  }
};

onMounted(async () => {
  await loadCounselors();
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
