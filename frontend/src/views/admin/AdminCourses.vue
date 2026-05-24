<template>
  <div class="courses-page">
    <div class="crumb">首页 <span>/</span> 课程管理</div>
    <h1 style="margin-bottom: 20px;">课程管理</h1>

    <el-card class="box-card" shadow="hover">
      <div class="toolbar">
        <el-button v-permission="'course:create'" type="success" icon="Plus" @click="openCreate">新增课程</el-button>
        <el-button @click="loadData" icon="Refresh">刷新</el-button>
      </div>

      <el-table :data="list" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseCode" label="课程编码" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column label="任课教师" min-width="140">
          <template #default="{ row }">
            {{ row.teacherName || '未分配教师' }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="上课地点" />
        <el-table-column label="上课时间">
          <template #default="{ row }">
            <el-tag size="small" type="info">周{{ row.weekDay }}</el-tag>
            <span style="margin-left: 8px">{{ row.startTime }} - {{ row.endTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'course:update'"
              link
              type="primary"
              icon="Edit"
              @click="openEdit(row)"
            >编辑</el-button>
            <el-popconfirm title="确认删除该课程吗？" @confirm="remove(row.id)">
              <template #reference>
                <el-button v-permission="'course:delete'" link type="danger" icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无课程数据" />
        </template>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="formMode === 'create' ? '新增课程' : '编辑课程'"
      width="500px"
      @close="closeDialog"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="课程编码" prop="courseCode">
          <el-input v-model.trim="form.courseCode" :disabled="formMode === 'edit'" placeholder="如：SE1001" />
        </el-form-item>

        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model.trim="form.courseName" placeholder="如：软件工程导论" />
        </el-form-item>

        <el-form-item label="任课教师" prop="teacherId">
          <el-select
            v-model="form.teacherId"
            placeholder="请选择教师"
            style="width: 100%"
          >
            <el-option
              v-for="item in teacherOptions"
              :key="item.id"
              :label="`${item.realName}（${item.username}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="上课地点" prop="location">
          <el-input v-model.trim="form.location" placeholder="如：教一-101" />
        </el-form-item>

        <el-form-item label="星期" prop="weekDay">
          <el-select v-model="form.weekDay" placeholder="请选择" style="width: 100%">
            <el-option label="星期一" :value="1" />
            <el-option label="星期二" :value="2" />
            <el-option label="星期三" :value="3" />
            <el-option label="星期四" :value="4" />
            <el-option label="星期五" :value="5" />
            <el-option label="星期六" :value="6" />
            <el-option label="星期日" :value="7" />
          </el-select>
        </el-form-item>

        <el-form-item label="上课时间" required>
          <div style="display: flex; gap: 10px; width: 100%">
            <el-time-picker v-model="form.startTime" placeholder="开始时间" format="HH:mm:ss" value-format="HH:mm:ss" style="flex: 1" />
            <span>至</span>
            <el-time-picker v-model="form.endTime" placeholder="结束时间" format="HH:mm:ss" value-format="HH:mm:ss" style="flex: 1" />
          </div>
        </el-form-item>

        <el-form-item v-if="formMode === 'edit'" label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="启用" :value="1" />
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
  createCourseApi,
  deleteCourseApi,
  listCoursesApi,
  listUsersByRoleApi,
  updateCourseApi
} from '../../api/adminTeaching';

const list = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const formMode = ref('create');
const formRef = ref(null);
const teacherOptions = ref([]);

const form = reactive({
  id: null,
  courseCode: '',
  courseName: '',
  teacherId: null,
  location: '',
  weekDay: 1,
  startTime: '08:00:00',
  endTime: '09:40:00',
  status: 1
});

const rules = {
  courseCode: [{ required: true, message: '请输入课程编码', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  teacherId: [{ required: true, message: '请选择任课教师', trigger: 'change' }],
  location: [{ required: true, message: '请输入上课地点', trigger: 'blur' }]
};

const loadTeachers = async () => {
  try {
    const data = await listUsersByRoleApi('TEACHER');
    teacherOptions.value = Array.isArray(data) ? data : [];
  } catch {
    teacherOptions.value = [];
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    list.value = await listCoursesApi();
  } catch (error) {
    ElMessage.error(error.message || '加载失败');
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields();
  form.id = null;
  form.courseCode = '';
  form.courseName = '';
  form.teacherId = null;
  form.location = '';
  form.weekDay = 1;
  form.startTime = '08:00:00';
  form.endTime = '09:40:00';
  form.status = 1;
};

const openCreate = async () => {
  formMode.value = 'create';
  resetForm();
  await loadTeachers();
  dialogVisible.value = true;
};

const openEdit = async (row) => {
  formMode.value = 'edit';
  resetForm();
  await loadTeachers();
  form.id = row.id;
  form.courseCode = row.courseCode;
  form.courseName = row.courseName;
  form.teacherId = row.teacherId ? Number(row.teacherId) : null;
  form.location = row.location || '';
  form.weekDay = row.weekDay;
  form.startTime = row.startTime;
  form.endTime = row.endTime;
  form.status = row.status;
  dialogVisible.value = true;
};

const closeDialog = () => {
  dialogVisible.value = false;
};

const submitForm = async () => {
  if (!formRef.value) return;

  if (!form.startTime || !form.endTime) {
    ElMessage.warning('请选择完整的上课时间');
    return;
  }

  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const payload = {
      courseCode: form.courseCode,
      courseName: form.courseName,
      teacherId: form.teacherId,
      location: form.location,
      weekDay: form.weekDay,
      startTime: form.startTime,
      endTime: form.endTime,
      status: form.status
    };

    if (formMode.value === 'create') {
      await createCourseApi(payload);
      ElMessage.success('新增课程成功');
    } else {
      await updateCourseApi(form.id, payload);
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
    await deleteCourseApi(id);
    ElMessage.success('删除成功');
    loadData();
  } catch (error) {
    ElMessage.error(error.message || '删除失败');
  }
};

onMounted(async () => {
  await loadTeachers();
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
