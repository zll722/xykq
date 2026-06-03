<template>
  <div class="attendance-records-page">
    <div class="crumb">首页 <span>/</span> 考勤记录</div>
    <h1 style="margin-bottom: 20px;">考勤记录管理</h1>

    <el-card class="box-card" shadow="hover">
      <div class="toolbar">
        <el-select v-model="filters.courseId" placeholder="选择课程" clearable style="width: 150px">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
        <el-select v-model="filters.classId" placeholder="选择班级" clearable style="width: 150px">
          <el-option v-for="c in classes" :key="c.id" :label="c.className" :value="c.id" />
        </el-select>

        <el-date-picker
          v-model="filters.attendanceDate"
          type="date"
          placeholder="考勤日期"
          value-format="YYYY-MM-DD"
          clearable
          style="width: 180px"
        />

        <el-select v-model="filters.status" placeholder="考勤状态" clearable style="width: 150px">
          <el-option label="正常 (PRESENT)" value="PRESENT" />
          <el-option label="迟到 (LATE)" value="LATE" />
          <el-option label="缺勤 (ABSENT)" value="ABSENT" />
          <el-option label="请假 (LEAVE)" value="LEAVE" />
        </el-select>

        <el-button type="primary" @click="loadData" icon="Search">查询</el-button>
        <el-button v-permission="'stats:export'" type="success" @click="download" icon="Download">导出Excel</el-button>
      </div>

      <el-table :data="list" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="className" label="班级" />
        <el-table-column label="学生" min-width="140">
          <template #default="{ row }">
            {{ row.studentName || '未命名学生' }}
          </template>
        </el-table-column>
        <el-table-column prop="attendanceDate" label="日期" width="120" />
        <el-table-column label="签到/签退时间" width="200">
          <template #default="{ row }">
            <div style="font-size: 12px; line-height: 1.2;">
              <div>入: {{ row.signInTime || '-' }}</div>
              <div>退: {{ row.signOutTime || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无考勤记录" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { exportAttendanceApi, listAdminAttendanceRecordsApi } from '../../api/adminStats';
import { listClassesApi, listCoursesApi } from '../../api/adminTeaching';

const list = ref([]);
const classes = ref([]);
const courses = ref([]);
const loading = ref(false);

const filters = reactive({
  courseId: null,
  classId: null,
  attendanceDate: '',
  status: ''
});

const loadData = async () => {
  loading.value = true;
  try {
    const params = { ...filters };
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === '') {
        delete params[key];
      }
    });
    list.value = await listAdminAttendanceRecordsApi(params);
  } catch (error) {
    ElMessage.error(error.message || '加载失败');
  } finally {
    loading.value = false;
  }
};

const download = async () => {
  try {
    const blob = await exportAttendanceApi({
      courseId: filters.courseId || undefined,
      classId: filters.classId || undefined,
      startDate: filters.attendanceDate || undefined,
      endDate: filters.attendanceDate || undefined
    });
    const href = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = href;
    a.download = 'attendance_export.xlsx';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(href);
    ElMessage.success('导出成功');
  } catch (error) {
    ElMessage.error(error.message || '导出失败');
  }
};

const getStatusType = (status) => {
  const map = {
    PRESENT: 'success',
    正常: 'success',
    LATE: 'warning',
    迟到: 'warning',
    ABSENT: 'danger',
    缺勤: 'danger',
    LEAVE: 'info',
    请假: 'info'
  };
  return map[status] || '';
};

const getStatusLabel = (status) => {
  const map = {
    PRESENT: '正常',
    LATE: '迟到',
    ABSENT: '缺勤',
    LEAVE: '请假'
  };
  return map[status] || status;
};

onMounted(() => {
  listClassesApi().then(res => classes.value = res);
  listCoursesApi().then(res => courses.value = res);
  loadData();
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
