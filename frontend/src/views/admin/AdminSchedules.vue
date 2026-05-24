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
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'schedule:update'"
              link
              type="primary"
              icon="Edit"
              @click="openEdit(row)"
            >编辑</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="formMode === 'create' ? '新增排班' : '编辑排班'"
      width="520px"
      @close="closeDialog"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <el-select
            v-model="form.courseId"
            placeholder="请选择课程"
            style="width: 100%"
            @change="onCourseChange"
          >
            <el-option
              v-for="item in courseOptions"
              :key="item.id"
              :label="`${item.courseName}（${item.teacherName || '未分配教师'}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="班级" prop="classIds">
          <el-select
            v-model="form.classIds"
            placeholder="请选择班级"
            style="width: 100%"
            :multiple="formMode === 'create'"
            collapse-tags
            collapse-tags-tooltip
          >
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="周次" prop="weekNo">
          <el-input-number
            v-model="form.weekNo"
            :min="1"
            :max="30"
            style="width: 100%"
            placeholder="留空表示整学期"
            controls-position="right"
          />
          <div style="font-size:12px;color:#909399;margin-top:4px">不填则表示整学期每周都上课</div>
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
            <span style="line-height:32px">至</span>
            <el-time-picker v-model="form.endTime" placeholder="结束时间" format="HH:mm:ss" value-format="HH:mm:ss" style="flex: 1" />
          </div>
          <div style="font-size:12px;color:#909399;margin-top:4px">选择课程后自动带入默认时间，可手动修改</div>
        </el-form-item>

        <el-form-item label="上课地点">
          <el-input v-model.trim="form.location" placeholder="留空则使用课程默认地点" />
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
  createScheduleApi,
  deleteScheduleApi,
  listClassesApi,
  listCoursesApi,
  listSchedulesApi,
  updateScheduleApi
} from '../../api/adminTeaching';

const list = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const formMode = ref('create');
const formRef = ref(null);
const courseOptions = ref([]);
const classOptions = ref([]);

const form = reactive({
  id: null,
  courseId: null,
  classIds: [],
  weekNo: null,
  weekDay: 1,
  startTime: '08:00:00',
  endTime: '09:40:00',
  location: ''
});

const rules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  classIds: [{ required: true, type: 'array', min: 1, message: '请选择班级', trigger: 'change' }],
  weekDay: [{ required: true, message: '请选择星期', trigger: 'change' }]
};

const loadOptions = async () => {
  const [courses, classes] = await Promise.all([
    listCoursesApi().catch(() => []),
    listClassesApi().catch(() => [])
  ]);
  courseOptions.value = Array.isArray(courses) ? courses : [];
  classOptions.value = Array.isArray(classes) ? classes.filter((c) => c.status === 1) : [];
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

/**
 * 选择课程时自动带入该课程的默认星期和上课时间
 */
const onCourseChange = (courseId) => {
  const course = courseOptions.value.find((c) => c.id === courseId);
  if (!course) return;
  if (course.weekDay) form.weekDay = course.weekDay;
  if (course.startTime) form.startTime = course.startTime;
  if (course.endTime) form.endTime = course.endTime;
  if (course.location && !form.location) form.location = course.location;
};

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields();
  form.id = null;
  form.courseId = null;
  form.classIds = [];
  form.weekNo = null;
  form.weekDay = 1;
  form.startTime = '08:00:00';
  form.endTime = '09:40:00';
  form.location = '';
};

const openCreate = async () => {
  formMode.value = 'create';
  resetForm();
  await loadOptions();
  dialogVisible.value = true;
};

const openEdit = async (row) => {
  formMode.value = 'edit';
  resetForm();
  await loadOptions();
  form.id = row.id;
  form.courseId = row.courseId ? Number(row.courseId) : null;
  form.classIds = row.classId ? [Number(row.classId)] : [];
  form.weekNo = row.weekNo || null;
  form.weekDay = row.weekDay;
  form.startTime = row.startTime;
  form.endTime = row.endTime;
  form.location = row.location || '';
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
    const basePayload = {
      courseId: form.courseId,
      weekNo: form.weekNo || null,
      weekDay: form.weekDay,
      startTime: form.startTime,
      endTime: form.endTime,
      location: form.location || null
    };

    if (formMode.value === 'create') {
      await Promise.all(
        form.classIds.map((classId) => createScheduleApi({ ...basePayload, classId }))
      );
      ElMessage.success(`新增排班成功，共 ${form.classIds.length} 个班级`);
    } else {
      await updateScheduleApi(form.id, { ...basePayload, classId: form.classIds[0] });
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
    await deleteScheduleApi(id);
    ElMessage.success('删除成功');
    loadData();
  } catch (error) {
    ElMessage.error(error.message || '删除失败');
  }
};

onMounted(async () => {
  await loadOptions();
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
