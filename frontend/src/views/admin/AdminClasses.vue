<template>
  <div class="classes-page">
    <div class="crumb">首页 <span>/</span> 班级管理</div>
    <h1 style="margin-bottom: 20px;">班级管理</h1>

    <el-card class="box-card" shadow="hover">
      <!-- Toolbar -->
      <div class="toolbar">
        <el-button v-permission="'class:create'" type="success" icon="Plus" @click="openCreate">新增班级</el-button>
        <el-button @click="loadData" icon="Refresh">刷新</el-button>
      </div>

      <!-- Data Table -->
      <el-table :data="list" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="classCode" label="班级编码" />
        <el-table-column prop="className" label="班级名称" />
        <el-table-column prop="capacity" label="容量/人数" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
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

    <!-- Create Dialog -->
    <el-dialog
      v-model="dialogVisible"
      title="新增班级"
      width="400px"
      @close="closeDialog"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="班级编码" prop="classCode">
          <el-input v-model.trim="form.classCode" placeholder="如：CS202601" />
        </el-form-item>
        
        <el-form-item label="班级名称" prop="className">
          <el-input v-model.trim="form.className" placeholder="如：计算机科学26级1班" />
        </el-form-item>
        
        <el-form-item label="班级人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="200" style="width: 100%" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="createClass" :loading="submitting">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { createClassApi, deleteClassApi, listClassesApi } from '../../api/adminTeaching';

const list = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const formRef = ref(null);

const form = reactive({
  classCode: '',
  className: '',
  capacity: 30
});

const rules = {
  classCode: [{ required: true, message: '请输入班级编码', trigger: 'blur' }],
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
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
  if (formRef.value) {
    formRef.value.resetFields();
  }
  form.classCode = '';
  form.className = '';
  form.capacity = 30;
};

const openCreate = () => {
  resetForm();
  dialogVisible.value = true;
};

const closeDialog = () => {
  dialogVisible.value = false;
};

const createClass = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        await createClassApi({
          classCode: form.classCode,
          className: form.className,
          capacity: form.capacity,
          status: 1
        });
        ElMessage.success('新增班级成功');
        closeDialog();
        loadData();
      } catch (error) {
        ElMessage.error(error.message || '新增失败');
      } finally {
        submitting.value = false;
      }
    }
  });
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

onMounted(loadData);
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
