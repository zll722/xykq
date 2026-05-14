<template>
  <div class="schedules-page">
    <div class="crumb">首页 <span>/</span> 排班管理</div>
    <h1 style="margin-bottom: 20px;">排班管理</h1>

    <el-card class="box-card" shadow="hover">
      <div class="toolbar">
        <el-button v-permission="'schedule:create'" type="success" icon="Plus" @click="openCreate">新增排班</el-button>
        <el-button @click="loadData" icon="Refresh">刷新</el-button>
      </div>

      <el-table :data="list" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" min-width="160" />
        <el-table-column prop="className" label="班级名称" min-width="170" />
        <el-table-column prop="weekNo" label="周次" width="100">
          <template #default="{ row }">
            {{ row.weekNo || '所有周' }}
          </template>
        </el-table-column>
        <el-table-column label="上课时间" min-width="220">
          <template #default="{ row }">
            <el-tag size="small" type="info">周{{ row.weekDay }}</el-tag>
            <span style="margin-left: 8px">{{ row.startTime }} - {{ row.endTime }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" min-width="150" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确认删除该排班吗？" @confirm="remove(row.id)">
              <template #reference>
                <el-button v-permission="'schedule:delete'" link type="danger" icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无排班数据" />
        </template>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增排班" width="500px" @close="closeDialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="课程ID" prop="courseId">
          <el-input-number v-model="form.courseId" :min="1" style="width: 100%" />
        </el-form-item>

        <el-form-item label="班级ID" prop="classId">
          <el-input-number v-model="form.classId" :min="1" style="width: 100%" />
        </el-form-item>

        <el-form-item label="周次" prop="weekNo">
          <el-input-number v-model="form.weekNo" :min="1" :max="30" placeholder="留空表示所有周" style="width: 100%" />
        </el-form-item>

        <el-form-item label="上课地点" prop="location">
          <el-input v-model.trim="form.location" placeholder="可覆盖课程默认地点" />
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
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="createSchedule" :loading="submitting">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { createScheduleApi, deleteScheduleApi, listSchedulesApi } from '../../api/adminTeaching';

const list = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const formRef = ref(null);

const form = reactive({
  courseId: null,
  classId: null,
  weekNo: null,
  weekDay: 1,
  startTime: '08:00:00',
  endTime: '09:40:00',
  location: ''
});

const rules = {
  courseId: [{ required: true, message: '请输入课程ID', trigger: 'blur' }],
  classId: [{ required: true, message: '请输入班级ID', trigger: 'blur' }]
};

const loadData = async () => {
  loading.value = true;
  try {
    list.value = await listSchedulesApi();
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
  form.courseId = null;
  form.classId = null;
  form.weekNo = null;
  form.weekDay = 1;
  form.startTime = '08:00:00';
  form.endTime = '09:40:00';
  form.location = '';
};

const openCreate = () => {
  resetForm();
  dialogVisible.value = true;
};

const closeDialog = () => {
  dialogVisible.value = false;
};

const createSchedule = async () => {
  if (!formRef.value) return;

  if (!form.startTime || !form.endTime) {
    ElMessage.warning('请选择完整的上课时间');
    return;
  }

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        await createScheduleApi({
          courseId: form.courseId,
          classId: form.classId,
          weekNo: form.weekNo,
          weekDay: form.weekDay,
          startTime: form.startTime,
          endTime: form.endTime,
          location: form.location
        });
        ElMessage.success('新增排班成功');
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
    await deleteScheduleApi(id);
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
